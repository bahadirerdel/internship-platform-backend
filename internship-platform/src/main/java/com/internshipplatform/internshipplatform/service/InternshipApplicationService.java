package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.entity.ApplicationStatus;
import com.internshipplatform.internshipplatform.entity.Company;
import com.internshipplatform.internshipplatform.entity.InternshipApplication;
import com.internshipplatform.internshipplatform.entity.VerificationStatus;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.exception.ForbiddenException;
import com.internshipplatform.internshipplatform.repository.CompanyRepository;
import com.internshipplatform.internshipplatform.repository.InternshipApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InternshipApplicationService {

    private final InternshipApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;
    private final NotificationService notificationService;

    @Transactional
    public void updateApplicationStatus(
            Long applicationId,
            Long companyUserId,
            ApplicationStatus newStatus
    ) {
        InternshipApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        // internship.company is User → compare userId
        Long ownerCompanyUserId = app.getInternship().getCompany().getId();

        if (!ownerCompanyUserId.equals(companyUserId)) {
            throw new ForbiddenException("You are not the owner of this internship");
        }

        if (app.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new ForbiddenException("Cannot change status of withdrawn application");
        }

        if (app.getStatus() == ApplicationStatus.ACCEPTED
                || app.getStatus() == ApplicationStatus.REJECTED) {
            throw new ForbiddenException("Cannot change status of finalized application");
        }

        // ✅ NEW: verification gate ONLY for ACCEPTED
        if (newStatus == ApplicationStatus.ACCEPTED) {

            Company company = companyRepository.findByUserId(companyUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Company profile not found"));

            if (company.getVerificationStatus() != VerificationStatus.APPROVED) {
                throw new ForbiddenException("Only verified companies can accept applicants");
            }
        }

        app.setStatus(newStatus);
        applicationRepository.save(app);
        Long studentUserId = app.getStudent().getId(); // in your model student id == userId
        String msg = "Your application for '" + app.getInternship().getTitle() +
                "' is now " + newStatus.name();

        notificationService.notifyUser(studentUserId, msg);
        String companyMsg = "You updated application #" + app.getId() +
                " to " + newStatus.name();
        notificationService.notifyUser(companyUserId, companyMsg);
    }
}

