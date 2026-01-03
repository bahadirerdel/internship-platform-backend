package com.internshipplatform.internshipplatform.dto;

import com.internshipplatform.internshipplatform.entity.DegreeLevel;
import com.internshipplatform.internshipplatform.entity.ExperienceLevel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPublicProfileDTO {
    private Long userId;
    private String name;

    // Education
    private String university;
    private String department;
    private DegreeLevel degreeLevel;
    private Integer graduationYear;
    private Double gpa;

    // Skills
    private String coreSkills;   // CSV
    private String otherSkills;  // CSV

    // Experience
    private ExperienceLevel experienceLevel;
    private Integer totalExperienceMonths;

    // Extras
    private String certifications; // CSV
    private String languages;      // CSV

    private String bio;
}
