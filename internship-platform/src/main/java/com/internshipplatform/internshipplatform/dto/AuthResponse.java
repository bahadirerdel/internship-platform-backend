package com.internshipplatform.internshipplatform.dto;

import com.internshipplatform.internshipplatform.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String message;   // "Registered successfully" or "Logged in"
    private String token;     // JWT (later)
    private Long userId;
    private String email;
    private Role role;
    private String name;
}