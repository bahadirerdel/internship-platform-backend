package com.internshipplatform.internshipplatform.dto;

import com.internshipplatform.internshipplatform.entity.DegreeLevel;
import com.internshipplatform.internshipplatform.entity.ExperienceLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 4000)
    private String description;

    private String location;

    private String internshipType;   // REMOTE / ONSITE / HYBRID
    private String salaryRange;

    // Skills
    private String requiredSkills;   // must-have (CSV)
    private String preferredSkills;  // nice-to-have (CSV)

    // Expectations
    private DegreeLevel minimumDegreeLevel;        // optional
    private ExperienceLevel minimumExperienceLevel; // optional

    // Textual details
    private String requirements;
    private String responsibilities;

    private LocalDate applicationDeadline;
}
