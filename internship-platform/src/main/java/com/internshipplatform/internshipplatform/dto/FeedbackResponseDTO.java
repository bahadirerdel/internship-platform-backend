package com.internshipplatform.internshipplatform.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedbackResponseDTO {
    private Long id;
    private Long applicationId;
    private Integer rating;
    private String comment;
    private String createdAt;
    private String updatedAt;
}
