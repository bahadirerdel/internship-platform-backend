package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.AdminActionResponse;
import com.internshipplatform.internshipplatform.dto.AdminVerifyCompanyRequest;
import com.internshipplatform.internshipplatform.dto.CompanyProfileDTO;
import com.internshipplatform.internshipplatform.security.JwtUtil;
import com.internshipplatform.internshipplatform.service.AdminCompanyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/companies")
@RequiredArgsConstructor
public class AdminCompanyController {

    private final AdminCompanyService adminCompanyService;
    private final JwtUtil jwtUtil;
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/verification-requests")
    public ResponseEntity<List<CompanyProfileDTO>> getPendingVerificationRequests() {
        return ResponseEntity.ok(adminCompanyService.getPendingVerificationRequests());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{companyId}/verify")
    public ResponseEntity<AdminActionResponse> verifyCompany(
            @PathVariable Long companyId,
            @RequestBody AdminVerifyCompanyRequest body,
            HttpServletRequest request
    ) {
        Long adminUserId = jwtUtil.getUserIdFromRequest(request);
        return ResponseEntity.ok(adminCompanyService.verifyCompany(companyId, adminUserId, body));
    }

}
