package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.*;
import com.internshipplatform.internshipplatform.entity.Role;
import com.internshipplatform.internshipplatform.entity.Student;
import com.internshipplatform.internshipplatform.entity.User;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.mapper.StudentMapper;
import com.internshipplatform.internshipplatform.repository.StudentRepository;
import com.internshipplatform.internshipplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final ResumeStorageService resumeStorageService;

    public StudentProfileResponse getMyProfile(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.STUDENT) {
            throw new RuntimeException("Current user is not a student");
        }

        Student student = studentRepository.findByUser(user)
                .orElseGet(() -> {
                    Student s = Student.builder()
                            .user(user)
                            .build();
                    return studentRepository.save(s);
                });

        // ✅ Backward compatibility: if coreSkills missing but legacy skills exists, auto-fill
        if (!isNotBlank(student.getCoreSkills()) && isNotBlank(student.getSkills())) {
            student.setCoreSkills(student.getSkills());
            studentRepository.save(student);
        }

        return studentMapper.toProfileResponse(student);
    }

    public StudentProfileResponse updateMyProfile(Long userId,
                                                  StudentProfileUpdateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.STUDENT) {
            throw new RuntimeException("Current user is not a student");
        }

        Student student = studentRepository.findByUser(user)
                .orElseGet(() -> {
                    Student s = Student.builder()
                            .user(user)
                            .build();
                    return studentRepository.save(s);
                });

        // Base fields
        student.setUniversity(request.getUniversity());
        student.setDepartment(request.getDepartment());
        student.setBio(request.getBio());

        // Education
        student.setDegreeLevel(request.getDegreeLevel());
        student.setGraduationYear(request.getGraduationYear());
        student.setGpa(request.getGpa());

        // Skills (new)
        student.setCoreSkills(request.getCoreSkills());
        student.setOtherSkills(request.getOtherSkills());

        // ✅ Keep legacy skills synced to coreSkills (so any old UI/pages using student.skills still work)
        student.setSkills(student.getCoreSkills());

        // Experience
        student.setExperienceLevel(request.getExperienceLevel());
        student.setTotalExperienceMonths(request.getTotalExperienceMonths());

        // Extras
        student.setCertifications(request.getCertifications());
        student.setLanguages(request.getLanguages());

        studentRepository.save(student);

        return studentMapper.toProfileResponse(student);
    }
    public void uploadResume(Long userId, MultipartFile file) {
        Student student = studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Delete old resume file if exists
        if (student.getResumeFileName() != null) {
            resumeStorageService.delete(student.getResumeFileName());
        }

        String storedName = resumeStorageService.saveResume(userId, file);

        // Stored filename (used to load file)
        student.setResumeFileName(storedName);

        // Original filename (used for download name)
        student.setResumeOriginalFileName(file.getOriginalFilename());

        // Metadata
        student.setResumeContentType(file.getContentType());
        student.setResumeSize(file.getSize());
        student.setResumeUploadedAt(Instant.now());

        studentRepository.save(student);
    }


    public void deleteResume(Long userId) {
        Student student = studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (student.getResumeFileName() != null) {
            resumeStorageService.delete(student.getResumeFileName());
        }

        student.setResumeFileName(null);
        student.setResumeContentType(null);
        student.setResumeSize(null);
        student.setResumeUploadedAt(null);

        studentRepository.save(student);
    }

    public ResumeFileDto downloadMyResume(Long userId) {

        Student student = studentRepository
                .findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (student.getResumeFileName() == null) {
            throw new ResourceNotFoundException("Resume not found");
        }

        // Load file from storage (you already have this)
        Resource resource = resumeStorageService.loadAsResource(student.getResumeFileName());

        ResumeFileDto dto = new ResumeFileDto();
        dto.setResource(resource);

        // ✅ ORIGINAL filename (better UX)
        dto.setFileName(student.getResumeOriginalFileName() != null
                ? student.getResumeOriginalFileName()
                : "resume.pdf");

        dto.setContentType(student.getResumeContentType() != null
                ? student.getResumeContentType()
                : "application/octet-stream");

        return dto;
    }
    public ProfileStrengthResponseDTO getMyProfileStrength(Long userId) {

        Student student = studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        int score = 0;
        List<String> missing = new java.util.ArrayList<>();

        // ✅ Backward compat: treat legacy skills as coreSkills if coreSkills empty
        String coreSkills = isNotBlank(student.getCoreSkills())
                ? student.getCoreSkills()
                : student.getSkills();

        // Weights (tweak anytime)
        score += addFieldScore(student.getUniversity(), 10, "university", missing);
        score += addFieldScore(student.getDepartment(), 10, "department", missing);

        score += (student.getGraduationYear() != null) ? 8 : addMissing("graduationYear", missing);
        score += addFieldScore(student.getBio(), 10, "bio", missing);

        // Skills (core is important, other is bonus)
        score += addFieldScore(coreSkills, 18, "coreSkills", missing);
        score += isNotBlank(student.getOtherSkills()) ? 5 : 0;

        // Education extras (optional)
        score += (student.getDegreeLevel() != null) ? 7 : addMissing("degreeLevel", missing);
        score += (student.getGpa() != null) ? 5 : 0;

        // Experience (optional but good)
        score += (student.getExperienceLevel() != null) ? 7 : addMissing("experienceLevel", missing);
        score += (student.getTotalExperienceMonths() != null) ? 5 : 0;

        // Extras (optional)
        score += isNotBlank(student.getCertifications()) ? 5 : 0;
        score += isNotBlank(student.getLanguages()) ? 3 : 0;

        // Resume upload presence
        score += (isNotBlank(student.getResumeFileName())) ? 20 : addMissing("resume", missing);

        if (score > 100) score = 100;

        return ProfileStrengthResponseDTO.builder()
                .score(score)
                .missingFields(missing)
                .build();
    }

    private int addFieldScore(String value, int points, String fieldName, List<String> missing) {
        if (isNotBlank(value)) return points;
        missing.add(fieldName);
        return 0;
    }

    private int addMissing(String fieldName, List<String> missing) {
        missing.add(fieldName);
        return 0;
    }
    public StudentPublicProfileDTO getPublicStudentProfile(Long studentUserId) {
        Student student = studentRepository.findByUser_Id(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        String coreSkills = isNotBlank(student.getCoreSkills())
                ? student.getCoreSkills()
                : student.getSkills();

        return StudentPublicProfileDTO.builder()
                .userId(student.getUser().getId())
                .name(student.getUser().getName())

                .university(student.getUniversity())
                .department(student.getDepartment())
                .degreeLevel(student.getDegreeLevel())
                .graduationYear(student.getGraduationYear())
                .gpa(student.getGpa())

                .coreSkills(coreSkills)
                .otherSkills(student.getOtherSkills())

                .experienceLevel(student.getExperienceLevel())
                .totalExperienceMonths(student.getTotalExperienceMonths())

                .certifications(student.getCertifications())
                .languages(student.getLanguages())

                .bio(student.getBio())
                .build();
    }



    private boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

}
