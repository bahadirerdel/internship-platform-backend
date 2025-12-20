package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.AdminUserDTO;
import com.internshipplatform.internshipplatform.dto.BlockUserRequest;
import com.internshipplatform.internshipplatform.entity.User;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.repository.UserRepository;
import com.internshipplatform.internshipplatform.security.JwtUtil;
import com.internshipplatform.internshipplatform.service.AdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;
    private final AdminUserService adminUserService;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/block")
    public ResponseEntity<?> blockUser(
            @PathVariable Long id,
            @RequestBody(required = false) BlockUserRequest body,
            HttpServletRequest request
    ) {
        Long adminUserId = jwtUtil.getUserIdFromRequest(request);
        String reason = body != null ? body.getReason() : null;

        adminUserService.blockUser(id, adminUserId, reason);
        return ResponseEntity.ok("User blocked");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {

        adminUserService.unblockUser(id);
        return ResponseEntity.ok("User unblocked");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<AdminUserDTO>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean blockedOnly
    ) {
        return ResponseEntity.ok(
                adminUserService.listUsers(role, blockedOnly, page, size, sortBy, sortDir)
        );
    }
}

