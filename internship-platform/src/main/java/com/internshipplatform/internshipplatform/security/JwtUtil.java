package com.internshipplatform.internshipplatform.security;

import com.internshipplatform.internshipplatform.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtService jwtService;

    public Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        // No token -> treat as anonymous
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) return null;

        // If token is invalid, extractUserId may throw -> caller catches
        return jwtService.extractUserId(token);
    }
}
