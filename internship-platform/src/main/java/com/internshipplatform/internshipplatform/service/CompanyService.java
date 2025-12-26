package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.CompanyProfileDTO;
import com.internshipplatform.internshipplatform.dto.CompanyPublicProfileDTO;
import com.internshipplatform.internshipplatform.entity.Company;
import com.internshipplatform.internshipplatform.entity.User;
import com.internshipplatform.internshipplatform.entity.VerificationStatus;
import com.internshipplatform.internshipplatform.exception.ForbiddenException;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.mapper.CompanyMapper;
import com.internshipplatform.internshipplatform.repository.CompanyRepository;
import com.internshipplatform.internshipplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyMapper companyMapper;

    /**
     * Get or auto-create the company profile for the given userId.
     */
    public CompanyProfileDTO getMyCompanyProfile(Long userId) {
        Company company = companyRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyCompanyForUser(userId));

        return companyMapper.toDto(company);
    }

    /**
     * Update (or create if missing) the company profile for the given userId.
     */
    public CompanyProfileDTO updateMyCompanyProfile(Long userId, CompanyProfileDTO request) {
        Company company = companyRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyCompanyForUser(userId));

        companyMapper.updateEntityFromDto(request, company);

        Company saved = companyRepository.save(company);
        return companyMapper.toDto(saved);
    }

    private Company createEmptyCompanyForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found for id " + userId));

        Company company = Company.builder()
                .userId(userId)
                // fallback name: user name or email if name null
                .name(user.getName() != null ? user.getName() : user.getEmail())
                .build();

        return companyRepository.save(company);
    }


    public void requestVerification(Long companyUserId) {

        Company company = companyRepository.findByUserId(companyUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (company.getVerificationStatus() == VerificationStatus.APPROVED) {
            throw new ForbiddenException("Company is already verified");
        }

        company.setVerificationStatus(VerificationStatus.PENDING);
        company.setVerificationRequestedAt(Instant.now());
        company.setVerificationReviewedAt(null);
        company.setVerificationNote(null);

        companyRepository.save(company);
    }
    public CompanyPublicProfileDTO getPublicCompanyProfile(Long companyUserId) {
        Company company = companyRepository.findByUserId(companyUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        VerificationStatus st = company.getVerificationStatus();

        return CompanyPublicProfileDTO.builder()
                .userId(company.getUserId())
                .name(company.getName())
                .industry(company.getIndustry())
                .website(company.getWebsite())
                .location(company.getLocation())
                .size(company.getSize())
                .description(company.getDescription())
                .logoUrl(company.getLogoUrl())
                .verificationStatus(st)
                .verified(st == VerificationStatus.APPROVED)
                .build();
    }


}
