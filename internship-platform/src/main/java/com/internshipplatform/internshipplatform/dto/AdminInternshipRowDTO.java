package com.internshipplatform.internshipplatform.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class AdminInternshipRowDTO {
    private Long id;
    private String title;
    private String companyEmail; // since company is a User
    private String location;

    private String visibilityStatus;
    private String hiddenReason;
    private Instant hiddenAt;
    private Long hiddenByAdminUserId;

    private Instant createdAt;
}
