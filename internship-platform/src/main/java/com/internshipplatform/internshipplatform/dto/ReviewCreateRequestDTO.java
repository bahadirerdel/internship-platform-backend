package com.internshipplatform.internshipplatform.dto;

import com.internshipplatform.internshipplatform.entity.ReviewType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateRequestDTO {
    private Long revieweeUserId;
    private ReviewType type;     // COMPANY_REVIEW or STUDENT_REVIEW
    private Integer rating;      // 1..5
    private String comment;      // optional
    private Boolean anonymous;
}
