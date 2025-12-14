package com.internshipplatform.internshipplatform.dto;

import com.internshipplatform.internshipplatform.entity.ReviewType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponseDTO {
    private Long id;
    private Long reviewerUserId;
    private Long revieweeUserId;
    private ReviewType type;
    private String reviewerName;
    private String reviewerRole;
    private Boolean anonymous;
    private Integer rating;
    private String comment;
    private String createdAt;
    private String updatedAt;
}
