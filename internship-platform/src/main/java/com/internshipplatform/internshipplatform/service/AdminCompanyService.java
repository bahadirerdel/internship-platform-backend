package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.AdminVerifyCompanyRequest;
import com.internshipplatform.internshipplatform.dto.CompanyProfileDTO;
import com.internshipplatform.internshipplatform.entity.Company;
import com.internshipplatform.internshipplatform.entity.VerificationStatus;
import com.internshipplatform.internshipplatform.exception.ForbiddenException;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCompanyService {

    private final CompanyRepository companyRepository;
    private final NotificationService notificationService;

    public List<CompanyProfileDTO> getPendingVerificationRequests() {
        return companyRepository.findAllByVerificationStatus(VerificationStatus.PENDING)
                .stream()
                .map(c -> CompanyProfileDTO.builder()
                        .id(c.getId())
                        .userId(c.getUserId())
                        .name(c.getName())
                        .industry(c.getIndustry())
                        .website(c.getWebsite())
                        .location(c.getLocation())
                        .size(c.getSize())
                        .description(c.getDescription())
                        .logoUrl(c.getLogoUrl())
                        .build())
                .toList();
    }

    public void verifyCompany(Long companyId, AdminVerifyCompanyRequest req) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        VerificationStatus status = req.getStatus();

        if (req.getStatus() != VerificationStatus.APPROVED && req.getStatus() != VerificationStatus.REJECTED) {
            throw new ForbiddenException("Status must be APPROVED or REJECTED");
        }



        company.setVerificationStatus(req.getStatus());
        company.setVerificationReviewedAt(Instant.now());
        company.setVerificationNote(req.getNote());

        companyRepository.save(company);

        Long companyUserId = company.getUserId();

        String msg;
        if (status == VerificationStatus.APPROVED) {
            msg = "✅ Your company verification request was APPROVED.";
        } else {
            String note = (req.getNote() != null && !req.getNote().isBlank())
                    ? " Reason: " + req.getNote()
                    : "";
            msg = "❌ Your company verification request was REJECTED." + note;
        }

        notificationService.notifyUser(companyUserId, msg);
    }
}
