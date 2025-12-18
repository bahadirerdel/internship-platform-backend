package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.InterviewResponseDTO;
import com.internshipplatform.internshipplatform.dto.ScheduleInterviewRequest;
import com.internshipplatform.internshipplatform.entity.*;
import com.internshipplatform.internshipplatform.exception.ForbiddenException;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.repository.InterviewRepository;
import com.internshipplatform.internshipplatform.repository.InternshipApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final InternshipApplicationRepository applicationRepository;
    private final NotificationService notificationService;

    /**
     * Company schedules or updates interview for an application.
     */
    @Transactional
    public InterviewResponseDTO scheduleOrUpdateInterview(
            Long applicationId,
            Long companyUserId,
            ScheduleInterviewRequest req
    ) {
        InternshipApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        Long ownerCompanyUserId = app.getInternship().getCompany().getId();
        if (!ownerCompanyUserId.equals(companyUserId)) {
            throw new ForbiddenException("You are not the owner of this internship");
        }

        if (app.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new ForbiddenException("Cannot schedule interview for withdrawn application");
        }
        if (app.getStatus() == ApplicationStatus.ACCEPTED || app.getStatus() == ApplicationStatus.REJECTED) {
            throw new ForbiddenException("Cannot schedule interview for finalized application");
        }

        Instant scheduledAt;
        try {
            scheduledAt = Instant.parse(req.getScheduledAt()); // expects "...Z"
        } catch (Exception e) {
            throw new ForbiddenException("Invalid scheduledAt format. Use ISO-8601 UTC like 2025-12-20T14:00:00Z");
        }

        Interview interview = interviewRepository.findByApplication_Id(applicationId)
                .orElseGet(() -> Interview.builder()
                        .application(app)
                        .status(Interview.InterviewStatus.SCHEDULED) // ✅ IMPORTANT
                        .build());

        // If it already existed but status was null due to old bad rows/code, fix it too:
        if (interview.getStatus() == null) {
            interview.setStatus(Interview.InterviewStatus.SCHEDULED);
        }

        interview.setScheduledAt(scheduledAt);
        interview.setMeetingLink(req.getMeetingLink());

        Interview saved = interviewRepository.save(interview);

        // ✅ Your model: Student has userId field, not User relation
        Long studentUserId = app.getStudent().getId();

        String msg = "Interview scheduled for '" + app.getInternship().getTitle() +
                "' at " + saved.getScheduledAt();
        notificationService.notifyUser(studentUserId, msg);

        String companyMsg = "You scheduled an interview for application #" + app.getId() +
                " at " + saved.getScheduledAt();
        notificationService.notifyUser(companyUserId, companyMsg);

        if (app.getStatus() != ApplicationStatus.INTERVIEW) {
            app.setStatus(ApplicationStatus.INTERVIEW);
            applicationRepository.save(app);
        }

        return toResponseDTO(saved);
    }


    /**
     * Student or company can view interview for an application if they are a party.
     */
    @Transactional(readOnly = true)
    public InterviewResponseDTO getInterview(Long applicationId, Long requesterUserId) {
        InternshipApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        Long companyUserId = app.getInternship().getCompany().getId();
        Long studentUserId = app.getStudent().getId();

        if (!requesterUserId.equals(companyUserId) && !requesterUserId.equals(studentUserId)) {
            throw new ForbiddenException("You are not allowed to view this interview");
        }

        Interview interview = interviewRepository.findByApplication_Id(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not scheduled yet"));

        return toResponseDTO(interview);
    }
    @Transactional(readOnly = true)
    public List<InterviewResponseDTO> getMyStudentInterviews(Long studentUserId) {
        return interviewRepository
                .findAllByApplication_Student_IdOrderByScheduledAtAsc(studentUserId)
                .stream()
                .map(i -> InterviewResponseDTO.builder()
                        .id(i.getId())
                        .applicationId(i.getApplication().getId())
                        .scheduledAt(i.getScheduledAt().toString())
                        .meetingLink(i.getMeetingLink())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InterviewResponseDTO> getMyCompanyInterviews(Long companyUserId) {
        return interviewRepository
                .findAllByApplication_Internship_Company_IdOrderByScheduledAtAsc(companyUserId)
                .stream()
                .map(i -> InterviewResponseDTO.builder()
                        .id(i.getId())
                        .applicationId(i.getApplication().getId())
                        .scheduledAt(i.getScheduledAt().toString())
                        .meetingLink(i.getMeetingLink())
                        .build())
                .collect(Collectors.toList());
    }
    @Transactional
    public InterviewResponseDTO cancelInterview(
            Long applicationId,
            Long companyUserId,
            String reason
    ) {

        InternshipApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        // Ownership check (correct)
        Long ownerCompanyUserId = app.getInternship().getCompany().getId();
        if (!ownerCompanyUserId.equals(companyUserId)) {
            throw new ForbiddenException("You are not the owner of this internship");
        }

        Interview interview = interviewRepository.findByApplication_Id(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not scheduled yet"));

        // ✅ Prevent double cancellation
        if (interview.getStatus() == Interview.InterviewStatus.CANCELLED) {
            throw new ForbiddenException("Interview already cancelled");
        }

        // ✅ Cancel interview
        interview.setStatus(Interview.InterviewStatus.CANCELLED);
        interview.setCancelReason(reason);
        interview.setCancelledAt(Instant.now());
        interview.setCancelledByUserId(companyUserId);

        Interview saved = interviewRepository.save(interview);
        Long studentUserId = app.getStudent().getId();
        String msg = "Interview cancelled for '" + app.getInternship().getTitle() + "'.";
        notificationService.notifyUser(studentUserId, msg);

        String companyMsg = "You cancelled an interview #" + app.getId() +
                " at " + interview.getScheduledAt();
        notificationService.notifyUser(companyUserId, companyMsg);
        // ✅ Return response DTO
        return toResponseDTO(saved);
    }


    private InterviewResponseDTO toResponseDTO(Interview interview) {
        return InterviewResponseDTO.builder()
                .id(interview.getId())
                .applicationId(interview.getApplication().getId())
                .scheduledAt(
                        interview.getScheduledAt() != null
                                ? interview.getScheduledAt().toString()
                                : null
                )
                .meetingLink(interview.getMeetingLink())
                .status(interview.getStatus().name())
                .cancelReason(interview.getCancelReason())
                .cancelledAt(
                        interview.getCancelledAt() != null
                                ? interview.getCancelledAt().toString()
                                : null
                )
                .build();
    }

}
