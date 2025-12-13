package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.ResumeFileDto;
import com.internshipplatform.internshipplatform.dto.StudentProfileResponse;
import com.internshipplatform.internshipplatform.dto.StudentProfileUpdateRequest;
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
                    // lazily create an empty profile if not exists
                    Student s = Student.builder()
                            .user(user)
                            .build();
                    return studentRepository.save(s);
                });

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

        // apply updates
        student.setUniversity(request.getUniversity());
        student.setDepartment(request.getDepartment());
        student.setBio(request.getBio());
        student.setSkills(request.getSkills());

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

        // ✅ Original filename (used for download name)
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


}
