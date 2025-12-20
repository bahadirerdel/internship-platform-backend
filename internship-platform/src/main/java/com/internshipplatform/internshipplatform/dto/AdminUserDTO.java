package com.internshipplatform.internshipplatform.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminUserDTO {
    private Long id;
    private String email;
    private String role;
    private boolean blocked;
    private String blockedReason;
    private String blockedAt; // ISO string
}