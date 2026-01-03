package com.internshipplatform.internshipplatform.mapper;

import com.internshipplatform.internshipplatform.dto.StudentProfileResponse;
import com.internshipplatform.internshipplatform.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public StudentProfileResponse toProfileResponse(Student student) {
        if (student == null) return null;

        var user = student.getUser();

        boolean hasResume = student.getResumeFileName() != null;

        // ✅ Backward compatibility:
        // If coreSkills empty but legacy skills exists, show legacy skills as coreSkills.
        String coreSkills = isNotBlank(student.getCoreSkills())
                ? student.getCoreSkills()
                : student.getSkills();

        return StudentProfileResponse.builder()
                .id(student.getId())
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())

                // Education
                .university(student.getUniversity())
                .department(student.getDepartment())
                .degreeLevel(student.getDegreeLevel())
                .graduationYear(student.getGraduationYear())
                .gpa(student.getGpa())

                // Skills
                .coreSkills(coreSkills)
                .otherSkills(student.getOtherSkills())
                .skills(student.getSkills()) // optional legacy field

                // Experience
                .experienceLevel(student.getExperienceLevel())
                .totalExperienceMonths(student.getTotalExperienceMonths())

                // Extras
                .certifications(student.getCertifications())
                .languages(student.getLanguages())

                // Resume mappings
                .resumeFileName(student.getResumeFileName())
                .resumeOriginalFileName(student.getResumeOriginalFileName())
                .resumeSize(student.getResumeSize())
                .resumeContentType(student.getResumeContentType())

                // Optional URL
                .resumeDownloadUrl(hasResume ? "/api/students/me/resume/download" : null)

                .bio(student.getBio())
                .build();
    }

    private boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
