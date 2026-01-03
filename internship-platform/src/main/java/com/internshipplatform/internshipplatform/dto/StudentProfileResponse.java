package com.internshipplatform.internshipplatform.dto;

import com.internshipplatform.internshipplatform.entity.DegreeLevel;
import com.internshipplatform.internshipplatform.entity.ExperienceLevel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileResponse {

    private Long id;        // student id
    private Long userId;    // linked user id
    private String email;
    private String name;
    private String role;

    // Education
    private String university;
    private String department;
    private DegreeLevel degreeLevel;
    private Integer graduationYear;
    private Double gpa;

    // Skills
    private String coreSkills;   // comma-separated
    private String otherSkills;  // comma-separated

    // legacy (optional; keep for backward compatibility)
    private String skills;

    // Experience
    private ExperienceLevel experienceLevel;
    private Integer totalExperienceMonths;

    // Extras
    private String certifications; // comma-separated
    private String languages;      // comma-separated

    // Resume metadata
    private String resumeFileName;
    private String resumeOriginalFileName;
    private Long resumeSize;
    private String resumeContentType;

    private String resumeDownloadUrl;

    private String bio;
}

