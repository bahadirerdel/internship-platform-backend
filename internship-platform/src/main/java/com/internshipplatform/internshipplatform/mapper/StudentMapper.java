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

        return StudentProfileResponse.builder()
                .id(student.getId())
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .university(student.getUniversity())
                .department(student.getDepartment())
                .graduationYear(student.getGraduationYear())
                .bio(student.getBio())
                .skills(student.getSkills())

                // ✅ resume mappings
                .resumeFileName(student.getResumeFileName())
                .resumeOriginalFileName(student.getResumeOriginalFileName())
                .resumeSize(student.getResumeSize())
                .resumeContentType(student.getResumeContentType())

                // ✅ optional URL (frontend can use it or ignore it)
                .resumeDownloadUrl(hasResume ? "/api/students/me/resume/download" : null)
                .build();
    }
}
