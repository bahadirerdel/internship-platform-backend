package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.ChangePasswordRequest;
import com.internshipplatform.internshipplatform.entity.User;
import com.internshipplatform.internshipplatform.exception.ForbiddenException;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.repository.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


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
    public UserResponseDTO getCurrentUser(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return userService.getUserById(userId);
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req,
                                            HttpServletRequest request) {

        Long userId = jwtUtil.getUserIdFromRequest(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new ForbiddenException("Current password is wrong");
        }

        if (!req.getNewPassword().equals(req.getConfirmNewPassword())) {
            throw new ForbiddenException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password updated");
    }
}

