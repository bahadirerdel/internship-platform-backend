package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.CompanyPublicProfileDTO;
import com.internshipplatform.internshipplatform.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class PublicCompanyController {

    private final CompanyService companyService;

    @GetMapping("/{companyUserId}/public")
    public CompanyPublicProfileDTO getPublicCompany(@PathVariable Long companyUserId) {
        return companyService.getPublicCompanyProfile(companyUserId);
    }
}

