package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.HideInternshipRequest;
import com.internshipplatform.internshipplatform.entity.Internship;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.repository.InternshipRepository;
import com.internshipplatform.internshipplatform.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/admin/internships")
@RequiredArgsConstructor
public class AdminInternshipController {

    private final InternshipRepository internshipRepository;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/hide")
    public ResponseEntity<?> hide(
            @PathVariable Long id,
            @RequestBody(required = false) HideInternshipRequest body,
            HttpServletRequest request
    ) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

        internship.setVisibilityStatus(Internship.InternshipVisibility.HIDDEN);
        internship.setHiddenReason(body != null ? body.getReason() : null);
        internship.setHiddenAt(Instant.now());
        internship.setHiddenByAdminUserId(jwtUtil.getUserIdFromRequest(request));

        internshipRepository.save(internship);
        return ResponseEntity.ok("Internship hidden");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/unhide")
    public ResponseEntity<?> unhide(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

        // optional: track who unhid it
        Long adminUserId = jwtUtil.getUserIdFromRequest(request);

        internship.setVisibilityStatus(Internship.InternshipVisibility.PUBLIC);
        internship.setHiddenReason(null);
        internship.setHiddenAt(null);
        internship.setHiddenByAdminUserId(null);

        internshipRepository.save(internship);
        return ResponseEntity.ok("Internship unhidden");
    }
}
