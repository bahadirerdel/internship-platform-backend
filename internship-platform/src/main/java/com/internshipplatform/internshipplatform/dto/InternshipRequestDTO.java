package com.internshipplatform.internshipplatform.dto;

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
    private String internshipType;   // REMOTE / ONSITE / HYBRID etc.
    private String salaryRange;
    private String requiredSkills;   // e.g. "Java, Spring, PostgreSQL"
    private String requirements;
    private String responsibilities;
    private LocalDate applicationDeadline;
}
