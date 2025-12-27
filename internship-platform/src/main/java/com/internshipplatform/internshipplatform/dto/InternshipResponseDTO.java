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
    private String companyName;      // from Username (or later CompanyProfile name)
    private String companyVerificationStatus; // "APPROVED", "PENDING", ...
    private Boolean companyVerified;          // true/false
    private String title;
    private String description;
    private String location;
    private String internshipType;
    private String salaryRange;
    private String requiredSkills;
    private LocalDate applicationDeadline;
    private String requirements;
    private String responsibilities;
    private Instant createdAt;
    private Instant updatedAt;
}
