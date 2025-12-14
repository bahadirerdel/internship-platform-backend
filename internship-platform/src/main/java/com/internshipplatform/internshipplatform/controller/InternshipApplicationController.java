package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.UpdateApplicationStatusRequest;
import com.internshipplatform.internshipplatform.security.JwtUtil;
import com.internshipplatform.internshipplatform.service.InternshipApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class InternshipApplicationController {

    private final InternshipApplicationService applicationService;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasRole('COMPANY')")
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<Void> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestBody UpdateApplicationStatusRequest body,
            HttpServletRequest request
    ) {
        Long companyUserId = jwtUtil.getUserIdFromRequest(request);
        applicationService.updateApplicationStatus(
                applicationId,
                companyUserId,
                body.getStatus()
        );
        return ResponseEntity.noContent().build();
    }
}
