package com.internshipplatform.internshipplatform.dto;

import com.internshipplatform.internshipplatform.entity.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ApplicationResponseDTO {

    private Long applicationId;
    private ApplicationStatus status;
    private Instant appliedAt;

    // For student: we show the internship details
    private InternshipResponseDTO internship;

    // For company: we may also show applicant info
    private Long studentId;
    private String studentEmail;
    private String studentName;
}
