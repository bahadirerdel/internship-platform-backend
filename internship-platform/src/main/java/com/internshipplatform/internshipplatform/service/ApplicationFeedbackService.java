package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.FeedbackRequestDTO;
import com.internshipplatform.internshipplatform.dto.FeedbackResponseDTO;
import com.internshipplatform.internshipplatform.entity.*;
import com.internshipplatform.internshipplatform.exception.ForbiddenException;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.repository.ApplicationFeedbackRepository;
import com.internshipplatform.internshipplatform.repository.InternshipApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationFeedbackService {

    private final ApplicationFeedbackRepository feedbackRepository;
    private final InternshipApplicationRepository applicationRepository;

    @Transactional
    public FeedbackResponseDTO createOrUpdateFeedback(Long applicationId, Long companyUserId, FeedbackRequestDTO req) {

        InternshipApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        // ownership: Internship.company is User
        Long ownerCompanyUserId = app.getInternship().getCompany().getId();
        if (!ownerCompanyUserId.equals(companyUserId)) {
            throw new ForbiddenException("You are not the owner of this internship");
        }

        // Application feedback: allow when a decision is made
        if (app.getStatus() != ApplicationStatus.ACCEPTED && app.getStatus() != ApplicationStatus.REJECTED) {
            throw new ForbiddenException("Feedback can only be given when application is ACCEPTED or REJECTED");
        }

        if (req.getRating() == null || req.getRating() < 1 || req.getRating() > 5) {
            throw new ForbiddenException("Rating must be between 1 and 5");
        }

        ApplicationFeedback feedback = feedbackRepository.findByApplication_Id(applicationId)
                .orElseGet(() -> ApplicationFeedback.builder().application(app).build());

        feedback.setRating(req.getRating());
        feedback.setComment(req.getComment());

        ApplicationFeedback saved = feedbackRepository.save(feedback);

        return FeedbackResponseDTO.builder()
                .id(saved.getId())
                .applicationId(applicationId)
                .rating(saved.getRating())
                .comment(saved.getComment())
                .createdAt(saved.getCreatedAt().toString())
                .updatedAt(saved.getUpdatedAt().toString())
                .build();
    }

    @Transactional(readOnly = true)
    public FeedbackResponseDTO getFeedback(Long applicationId, Long requesterUserId) {
        InternshipApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        Long companyUserId = app.getInternship().getCompany().getId();
        Long studentUserId = app.getStudent().getId(); // your model: student is User

        if (!requesterUserId.equals(companyUserId) && !requesterUserId.equals(studentUserId)) {
            throw new ForbiddenException("You are not allowed to view this feedback");
        }

        ApplicationFeedback feedback = feedbackRepository.findByApplication_Id(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found"));

        return FeedbackResponseDTO.builder()
                .id(feedback.getId())
                .applicationId(applicationId)
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedAt().toString())
                .updatedAt(feedback.getUpdatedAt().toString())
                .build();
    }
}
