package com.internshipplatform.internshipplatform.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class AdminActionResponse {
    @Size(max = 300)
    private String message;
    private Long adminUserId;
    private Instant at;
}
