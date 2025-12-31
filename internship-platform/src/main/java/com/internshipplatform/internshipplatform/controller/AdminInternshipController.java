package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.AdminActionResponse;
import com.internshipplatform.internshipplatform.dto.AdminInternshipRowDTO;
import com.internshipplatform.internshipplatform.dto.HideInternshipRequest;
import com.internshipplatform.internshipplatform.security.JwtUtil;
import com.internshipplatform.internshipplatform.service.AdminInternshipService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/internships")
@RequiredArgsConstructor
public class AdminInternshipController {

    private final AdminInternshipService adminInternshipService;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/hide")
    public ResponseEntity<AdminActionResponse> hide(
            @PathVariable Long id,
            @RequestBody(required = false) HideInternshipRequest body,
            HttpServletRequest request
    ) {
        Long adminUserId = jwtUtil.getUserIdFromRequest(request);
        String reason = body != null ? body.getReason() : null;

        return ResponseEntity.ok(
                adminInternshipService.hideInternship(id, adminUserId, reason)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/unhide")
    public ResponseEntity<AdminActionResponse> unhide(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long adminUserId = jwtUtil.getUserIdFromRequest(request);

        return ResponseEntity.ok(
                adminInternshipService.unhideInternship(id, adminUserId)
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<AdminInternshipRowDTO>> list(
            @RequestParam(required = false) String visibility,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ResponseEntity.ok(
                adminInternshipService.listInternshipsForAdmin(
                        visibility, q, page, size, sortBy, sortDir
                )
        );
    }


}
