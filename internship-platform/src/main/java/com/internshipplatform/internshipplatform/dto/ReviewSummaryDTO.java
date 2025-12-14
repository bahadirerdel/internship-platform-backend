package com.internshipplatform.internshipplatform.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewSummaryDTO {
    private Double averageRating;
    private Long totalReviews;
    private List<ReviewResponseDTO> reviews;
}
