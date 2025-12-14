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
}
