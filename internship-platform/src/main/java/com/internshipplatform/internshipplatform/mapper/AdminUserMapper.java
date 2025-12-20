package com.internshipplatform.internshipplatform.mapper;

import com.internshipplatform.internshipplatform.dto.AdminUserDTO;
import com.internshipplatform.internshipplatform.entity.User;

public class AdminUserMapper {

    private AdminUserMapper() {}

    public static AdminUserDTO toDto(User u) {
        return AdminUserDTO.builder()
                .id(u.getId())
                .email(u.getEmail())
                .role(u.getRole().name())
                .blocked(!u.isEnabled())
                .blockedReason(u.getBlockedReason())
                .blockedAt(
                        u.getBlockedAt() != null
                                ? u.getBlockedAt().toString()
                                : null
                )
                .build();
    }
}
