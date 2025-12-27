package com.internshipplatform.internshipplatform.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InterviewListItemDTO {
    private Long id;
    private Long applicationId;

    private String scheduledAt;
    private String meetingLink;

    private String status;
    private String cancelReason;
    private String cancelledAt;

    // ✅ context for UI
    private Long internshipId;
    private String internshipTitle;

    private Long companyUserId;
    private String companyName;

    private Long studentUserId;
    private String studentName;
    private String studentEmail;
}
