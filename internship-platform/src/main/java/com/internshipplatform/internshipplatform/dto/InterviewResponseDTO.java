package com.internshipplatform.internshipplatform.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InterviewResponseDTO {
    private Long id;
    private Long applicationId;

    private String scheduledAt;
    private String meetingLink;

    // ✅ new fields
    private String status;        // "SCHEDULED" or "CANCELLED"
    private String cancelReason;  // nullable
    private String cancelledAt;   // nullable (ISO string)
}
