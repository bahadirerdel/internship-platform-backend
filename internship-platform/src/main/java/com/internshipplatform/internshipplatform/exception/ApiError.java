package com.internshipplatform.internshipplatform.exception;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ApiError {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ApiError(int i, String s) {
    }
}
