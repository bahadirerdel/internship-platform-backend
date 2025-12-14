package com.internshipplatform.internshipplatform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRequestDTO {
    private Integer rating;   // 1..5
    private String comment;   // optional
}
