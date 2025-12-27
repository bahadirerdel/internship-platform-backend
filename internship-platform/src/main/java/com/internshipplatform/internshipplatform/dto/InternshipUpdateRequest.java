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
public class InternshipUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    @Size(max = 4000)
    private String description;

    private String location;        // can be null
    private String internshipType;  // REMOTE / ONSITE / HYBRID, etc.
    private String salaryRange;     // e.g. "Unpaid", "₺20k–₺30k"

    private String requiredSkills;  // e.g. "Java, Spring Boot, PostgreSQL"

    private LocalDate applicationDeadline;
    private String requirements;
    private String responsibilities;
}
