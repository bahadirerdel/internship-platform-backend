package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.AdminVerifyCompanyRequest;
import com.internshipplatform.internshipplatform.dto.CompanyProfileDTO;
import com.internshipplatform.internshipplatform.service.AdminCompanyService;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/verification-requests")
    public ResponseEntity<List<CompanyProfileDTO>> getPendingVerificationRequests() {
        return ResponseEntity.ok(adminCompanyService.getPendingVerificationRequests());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{companyId}/verify")
    public ResponseEntity<String> verifyCompany(
            @PathVariable Long companyId,
            @RequestBody AdminVerifyCompanyRequest body
    ) {
        adminCompanyService.verifyCompany(companyId, body);
        return ResponseEntity.ok("Company verification updated");
    }
}
