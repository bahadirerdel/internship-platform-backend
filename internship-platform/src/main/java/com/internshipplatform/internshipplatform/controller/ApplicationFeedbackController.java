package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.FeedbackRequestDTO;
import com.internshipplatform.internshipplatform.dto.FeedbackResponseDTO;
import com.internshipplatform.internshipplatform.security.JwtUtil;
import com.internshipplatform.internshipplatform.service.ApplicationFeedbackService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationFeedbackController {

    private final ApplicationFeedbackService feedbackService;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasRole('COMPANY')")
    @PostMapping("/{applicationId}/feedback")
    public ResponseEntity<FeedbackResponseDTO> createOrUpdateFeedback(
            @PathVariable Long applicationId,
            @RequestBody FeedbackRequestDTO body,
            HttpServletRequest request
    ) {
        Long companyUserId = jwtUtil.getUserIdFromRequest(request);
        return ResponseEntity.ok(feedbackService.createOrUpdateFeedback(applicationId, companyUserId, body));
    }

    @PreAuthorize("hasRole('COMPANY') or hasRole('STUDENT')")
    @GetMapping("/{applicationId}/feedback")
    public ResponseEntity<FeedbackResponseDTO> getFeedback(
            @PathVariable Long applicationId,
            HttpServletRequest request
    ) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return ResponseEntity.ok(feedbackService.getFeedback(applicationId, userId));
    }
}
