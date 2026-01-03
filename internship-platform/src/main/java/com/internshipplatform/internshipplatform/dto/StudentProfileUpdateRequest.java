package com.internshipplatform.internshipplatform.dto;

import com.internshipplatform.internshipplatform.entity.DegreeLevel;
import com.internshipplatform.internshipplatform.entity.ExperienceLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileUpdateRequest {

    private String university;
    private String department;
    private DegreeLevel degreeLevel;
    private Integer graduationYear;
    private Double gpa;

    private String coreSkills;
    private String otherSkills;

    private ExperienceLevel experienceLevel;
    private Integer totalExperienceMonths;

    private String certifications;
    private String languages;

    private String bio;
}
