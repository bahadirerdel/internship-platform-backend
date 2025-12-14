package com.internshipplatform.internshipplatform.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewSummaryPageDTO {
    private Double averageRating;
    private Long totalReviews;

    private Integer page;
    private Integer size;
    private Long totalPages;
    private Long totalElements;

    private List<ReviewResponseDTO> reviews;
}
