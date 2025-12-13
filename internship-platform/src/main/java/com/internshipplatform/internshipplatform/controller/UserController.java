package com.internshipplatform.internshipplatform.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.internshipplatform.internshipplatform.dto.UserCreateRequest;
import com.internshipplatform.internshipplatform.dto.UserResponseDTO;
import com.internshipplatform.internshipplatform.service.UserService;
import com.internshipplatform.internshipplatform.service.JwtService;
import com.internshipplatform.internshipplatform.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;   // or JwtUtil, if you prefer
    // GET /api/users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // POST /api/users
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public UserResponseDTO createUser(@RequestBody @Valid UserCreateRequest request) {
        return userService.createUser(request);
    }
    // 🔹 NEW: GET /api/users/me
    @GetMapping("/me")
    public UserResponseDTO getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // remove "Bearer "
        Long userId = jwtService.extractUserId(token);

        return userService.getUserById(userId);
    }
}

