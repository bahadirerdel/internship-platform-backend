package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.ReviewCreateRequestDTO;
import com.internshipplatform.internshipplatform.dto.ReviewResponseDTO;
import com.internshipplatform.internshipplatform.dto.ReviewSummaryDTO;
import com.internshipplatform.internshipplatform.dto.ReviewSummaryPageDTO;
import com.internshipplatform.internshipplatform.entity.ReviewType;
import com.internshipplatform.internshipplatform.security.JwtUtil;
import com.internshipplatform.internshipplatform.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtUtil jwtUtil;

    // Create/update a review (auth required)
    @PreAuthorize("hasRole('STUDENT') or hasRole('COMPANY')")
    @PostMapping("/reviews")
    public ResponseEntity<ReviewResponseDTO> createOrUpdateReview(
            @RequestBody ReviewCreateRequestDTO body,
            HttpServletRequest request
    ) {
        Long reviewerUserId = jwtUtil.getUserIdFromRequest(request);
        return ResponseEntity.ok(reviewService.createOrUpdateReview(reviewerUserId, body));
    }


    @GetMapping("/companies/{companyUserId}/reviews")
    public ResponseEntity<ReviewSummaryPageDTO> getCompanyReviews(
            @PathVariable Long companyUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(
                reviewService.getReviewSummaryForUserPaged(companyUserId, ReviewType.COMPANY_REVIEW, page, size)
        );
    }

    @GetMapping("/students/{studentUserId}/reviews")
    public ResponseEntity<ReviewSummaryPageDTO> getStudentReviews(
            @PathVariable Long studentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(
                reviewService.getReviewSummaryForUserPaged(studentUserId, ReviewType.STUDENT_REVIEW, page, size)
        );
    }

}
