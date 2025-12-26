package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.ResumeFileDto;
import com.internshipplatform.internshipplatform.dto.UpdateApplicationStatusRequest;
import com.internshipplatform.internshipplatform.security.JwtUtil;
import com.internshipplatform.internshipplatform.service.InternshipApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    @PreAuthorize("hasRole('COMPANY')")
    @GetMapping("/{applicationId}/resume/download")
    public ResponseEntity<Resource> downloadApplicantResume(
            @PathVariable Long applicationId,
            HttpServletRequest request
    ) {
        Long companyUserId = jwtUtil.getUserIdFromRequest(request);

        ResumeFileDto resume = applicationService.downloadApplicantResume(applicationId, companyUserId);

        // Safety: avoid header injection
        String safeFileName = resume.getFileName().replace("\"", "");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + safeFileName + "\"")
                .contentType(MediaType.parseMediaType(resume.getContentType()))
                .body(resume.getResource());
    }

}
