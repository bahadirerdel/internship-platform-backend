package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.entity.ApplicationStatus;
import com.internshipplatform.internshipplatform.entity.InternshipApplication;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.exception.ForbiddenException;
import com.internshipplatform.internshipplatform.repository.InternshipApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InternshipApplicationService {

    private final InternshipApplicationRepository applicationRepository;

    @Transactional
    public void updateApplicationStatus(
            Long applicationId,
            Long companyUserId,
            ApplicationStatus newStatus
    ) {
        InternshipApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

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

        app.setStatus(newStatus);
        applicationRepository.save(app);
    }

}
