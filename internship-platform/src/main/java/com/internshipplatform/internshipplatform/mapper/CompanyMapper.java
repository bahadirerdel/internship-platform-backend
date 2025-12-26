package com.internshipplatform.internshipplatform.mapper;

import com.internshipplatform.internshipplatform.dto.CompanyProfileDTO;
import com.internshipplatform.internshipplatform.entity.Company;
import com.internshipplatform.internshipplatform.entity.VerificationStatus;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyProfileDTO toDto(Company company) {
        if (company == null) return null;
        VerificationStatus status = company.getVerificationStatus();
        return CompanyProfileDTO.builder()
                .id(company.getId())
                .userId(company.getUserId())
                .name(company.getName())
                .industry(company.getIndustry())
                .website(company.getWebsite())
                .location(company.getLocation())
                .size(company.getSize())
                .description(company.getDescription())
                .logoUrl(company.getLogoUrl())
                .verificationStatus(status)
                .verificationRequestedAt(company.getVerificationRequestedAt())
                .verificationReviewedAt(company.getVerificationReviewedAt())
                .verificationNote(company.getVerificationNote())
                .verified(status == VerificationStatus.APPROVED)
                .build();
    }

    public void updateEntityFromDto(CompanyProfileDTO dto, Company company) {
        // only update fields that come from client
        if (dto.getName() != null)        company.setName(dto.getName());
        if (dto.getIndustry() != null)    company.setIndustry(dto.getIndustry());
        if (dto.getWebsite() != null)     company.setWebsite(dto.getWebsite());
        if (dto.getLocation() != null)    company.setLocation(dto.getLocation());
        if (dto.getSize() != null)        company.setSize(dto.getSize());
        if (dto.getDescription() != null) company.setDescription(dto.getDescription());
        if (dto.getLogoUrl() != null)     company.setLogoUrl(dto.getLogoUrl());
    }
}
