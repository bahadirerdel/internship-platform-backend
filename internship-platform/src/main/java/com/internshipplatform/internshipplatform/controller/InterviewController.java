package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.InterviewResponseDTO;
import com.internshipplatform.internshipplatform.dto.ScheduleInterviewRequest;
import com.internshipplatform.internshipplatform.security.JwtUtil;
import com.internshipplatform.internshipplatform.service.InterviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;
    private final JwtUtil jwtUtil;

    // Company schedules/updates interview
    @PreAuthorize("hasRole('COMPANY')")
    @PostMapping("/{applicationId}/interview")
    public ResponseEntity<InterviewResponseDTO> scheduleInterview(
            @PathVariable Long applicationId,
            @RequestBody ScheduleInterviewRequest body,
            HttpServletRequest request
    ) {
        Long companyUserId = jwtUtil.getUserIdFromRequest(request);
        InterviewResponseDTO dto = interviewService.scheduleOrUpdateInterview(applicationId, companyUserId, body);
        return ResponseEntity.ok(dto);
    }

    // Student or Company views interview for application
    @PreAuthorize("hasRole('STUDENT') or hasRole('COMPANY')")
    @GetMapping("/{applicationId}/interview")
    public ResponseEntity<InterviewResponseDTO> getInterview(
            @PathVariable Long applicationId,
            HttpServletRequest request
    ) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        InterviewResponseDTO dto = interviewService.getInterview(applicationId, userId);
        return ResponseEntity.ok(dto);
    }
}
