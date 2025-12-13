package com.internshipplatform.internshipplatform.dto;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipResponseDTO {

    private Long id;

    private Long companyId;
    private String companyName;      // from User name (or later CompanyProfile name)

    private String title;
    private String description;
    private String location;
    private String internshipType;
    private String salaryRange;
    private String requiredSkills;
    private LocalDate applicationDeadline;

    private Instant createdAt;
    private Instant updatedAt;
}
