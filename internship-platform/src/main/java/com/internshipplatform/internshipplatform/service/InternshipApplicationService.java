package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.ApplicationResponseDTO;
import com.internshipplatform.internshipplatform.dto.InternshipResponseDTO;
import com.internshipplatform.internshipplatform.dto.ResumeFileDto;
import com.internshipplatform.internshipplatform.entity.*;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.exception.ForbiddenException;
import com.internshipplatform.internshipplatform.repository.CompanyRepository;
import com.internshipplatform.internshipplatform.repository.InternshipApplicationRepository;
import com.internshipplatform.internshipplatform.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
public class InternshipApplicationService {

    private final InternshipApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;
    private final NotificationService notificationService;
    private final ResumeStorageService resumeStorageService;
    private final StudentRepository studentRepository;

    @Transactional
    public void updateApplicationStatus(Long applicationId, Long companyUserId, ApplicationStatus newStatus) {
        InternshipApplication app = getApplicationOrThrow(applicationId);
        assertCompanyOwnsInternship(app, companyUserId);

        if (app.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new ForbiddenException("Cannot change status of withdrawn application");
        }

        if (app.getStatus() == ApplicationStatus.ACCEPTED || app.getStatus() == ApplicationStatus.REJECTED) {
            throw new ForbiddenException("Cannot change status of finalized application");
        }

        // ✅ Gate only accepting behind verification
        if (newStatus == ApplicationStatus.ACCEPTED) {
            Company company = companyRepository.findByUserId(companyUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Company profile not found"));

            if (company.getVerificationStatus() != VerificationStatus.APPROVED) {
                throw new ForbiddenException("Only verified companies can accept applicants");
            }
        }

        app.setStatus(newStatus);
        applicationRepository.save(app);

        notifyStatusChange(app, companyUserId, newStatus);
    }

    public ResumeFileDto downloadApplicantResume(Long applicationId, Long companyUserId) {
        InternshipApplication app = getApplicationOrThrow(applicationId);
        assertCompanyOwnsInternship(app, companyUserId);

        Long studentUserId = app.getStudent().getId();

        Student student = studentRepository.findByUser_Id(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        String storedFileName = student.getResumeFileName();
        if (storedFileName == null) {
            throw new ResourceNotFoundException("Student has not uploaded a resume");
        }

        Resource resource = resumeStorageService.loadAsResource(storedFileName);

        ResumeFileDto dto = new ResumeFileDto();
        dto.setResource(resource);
        dto.setFileName(student.getResumeOriginalFileName() != null
                ? student.getResumeOriginalFileName()
                : storedFileName);
        dto.setContentType(student.getResumeContentType() != null
                ? student.getResumeContentType()
                : "application/octet-stream");

        return dto;
    }

    // ✅ NEW: Company interns list (ACCEPTED applications)
    public List<InternshipApplication> getMyAcceptedInterns(Long companyUserId) {
        return applicationRepository.findByInternship_Company_IdAndStatus(companyUserId, ApplicationStatus.ACCEPTED);
    }

    // ---------------- private helpers ----------------

    private InternshipApplication getApplicationOrThrow(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

    private void assertCompanyOwnsInternship(InternshipApplication app, Long companyUserId) {
        Long ownerCompanyUserId = app.getInternship().getCompany().getId();
        if (!ownerCompanyUserId.equals(companyUserId)) {
            throw new ForbiddenException("You are not the owner of this internship");
        }
    }

    private void notifyStatusChange(InternshipApplication app, Long companyUserId, ApplicationStatus status) {
        Long studentUserId = app.getStudent().getId();

        notificationService.notifyUser(
                studentUserId,
                "Your application for '" + app.getInternship().getTitle() + "' is now " + status.name()
        );

        notificationService.notifyUser(
                companyUserId,
                "You updated application #" + app.getId() + " to " + status.name()
        );
    }
    public List<ApplicationResponseDTO> getMyAcceptedInternsDto(Long companyUserId) {

        // You need a repo query that returns all ACCEPTED applications for internships owned by this company user
        List<InternshipApplication> apps =
                applicationRepository.findByInternship_Company_IdAndStatus(companyUserId, ApplicationStatus.ACCEPTED);

        return apps.stream()
                .map(app -> ApplicationResponseDTO.builder()
                        .applicationId(app.getId())
                        .status(app.getStatus())
                        .appliedAt(app.getAppliedAt())

                        .internship(InternshipResponseDTO.builder()
                                .id(app.getInternship().getId())
                                .title(app.getInternship().getTitle())
                                .location(app.getInternship().getLocation())

                                // ✅ FIX #1: remove .name() because it's a String in your project
                                .internshipType(app.getInternship().getInternshipType())

                                .applicationDeadline(app.getInternship().getApplicationDeadline())

                                .companyName(app.getInternship().getCompany().getName())
                                .companyId(app.getInternship().getCompany().getId()) // company userId in your model
                                .build()
                        )

                        // student info (these are USER fields in your model)
                        .studentId(app.getStudent().getId())
                        .studentName(app.getStudent().getName())
                        .studentEmail(app.getStudent().getEmail())

                        .build())
                // ✅ FIX #2: collect into List
                .collect(Collectors.toList());
    }

}


