package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.CompanyProfileDTO;
import com.internshipplatform.internshipplatform.dto.InterviewResponseDTO;
import com.internshipplatform.internshipplatform.security.JwtUtil;
import com.internshipplatform.internshipplatform.service.CompanyService;
import com.internshipplatform.internshipplatform.service.InterviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final JwtUtil jwtUtil;
    private final InterviewService interviewService;


    @PreAuthorize("hasRole('COMPANY')")
    @GetMapping("/me")
    public CompanyProfileDTO getMyCompanyProfile(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return companyService.getMyCompanyProfile(userId);
    }

    @PreAuthorize("hasRole('COMPANY')")
    @PutMapping("/me")
    public CompanyProfileDTO updateMyCompanyProfile(
            HttpServletRequest request,
            @RequestBody CompanyProfileDTO body
    ) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return companyService.updateMyCompanyProfile(userId, body);
    }
    @PreAuthorize("hasRole('COMPANY')")
    @GetMapping("/me/interviews")
    public ResponseEntity<List<InterviewResponseDTO>> getMyInterviews(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return ResponseEntity.ok(interviewService.getMyCompanyInterviews(userId));
    }

}
