package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.ReviewCreateRequestDTO;
import com.internshipplatform.internshipplatform.dto.ReviewResponseDTO;
import com.internshipplatform.internshipplatform.dto.ReviewSummaryDTO;
import com.internshipplatform.internshipplatform.dto.ReviewSummaryPageDTO;
import com.internshipplatform.internshipplatform.entity.*;
import com.internshipplatform.internshipplatform.exception.ForbiddenException;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.repository.InternshipApplicationRepository;
import com.internshipplatform.internshipplatform.repository.ReviewRepository;
import com.internshipplatform.internshipplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final InternshipApplicationRepository applicationRepository;

    @Transactional
    public ReviewResponseDTO createOrUpdateReview(Long reviewerUserId, ReviewCreateRequestDTO req) {

        if (req.getRevieweeUserId() == null) throw new ForbiddenException("revieweeUserId is required");
        if (req.getType() == null) throw new ForbiddenException("type is required");
        if (req.getRating() == null || req.getRating() < 1 || req.getRating() > 5) {
            throw new ForbiddenException("Rating must be between 1 and 5");
        }

        if (reviewerUserId.equals(req.getRevieweeUserId())) {
            throw new ForbiddenException("You cannot review yourself");
        }

        User reviewer = userRepository.findById(reviewerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));

        User reviewee = userRepository.findById(req.getRevieweeUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Reviewee not found"));

        // Role constraints (IMPORTANT)
        // - COMPANY_REVIEW: student reviews company
        // - STUDENT_REVIEW: company reviews student
        if (req.getType() == ReviewType.COMPANY_REVIEW) {
            if (reviewer.getRole() != Role.STUDENT || reviewee.getRole() != Role.COMPANY) {
                throw new ForbiddenException("COMPANY_REVIEW must be written by a STUDENT for a COMPANY");
            }
            // Must have ACCEPTED application between student (reviewer) and company (reviewee)
            applicationRepository.findFirstByStudent_IdAndInternship_Company_IdAndStatus(
                    reviewerUserId, req.getRevieweeUserId(), ApplicationStatus.ACCEPTED
            ).orElseThrow(() -> new ForbiddenException("You can only review a company after an ACCEPTED application"));
        } else { // STUDENT_REVIEW
            if (reviewer.getRole() != Role.COMPANY || reviewee.getRole() != Role.STUDENT) {
                throw new ForbiddenException("STUDENT_REVIEW must be written by a COMPANY for a STUDENT");
            }
            // Must have ACCEPTED application between student (reviewee) and company (reviewer)
            applicationRepository.findFirstByStudent_IdAndInternship_Company_IdAndStatus(
                    req.getRevieweeUserId(), reviewerUserId, ApplicationStatus.ACCEPTED
            ).orElseThrow(() -> new ForbiddenException("You can only review a student after an ACCEPTED application"));
        }

        Review review = reviewRepository.findByReviewer_IdAndReviewee_IdAndType(reviewerUserId, req.getRevieweeUserId(), req.getType())
                .orElseGet(() -> Review.builder()
                        .reviewer(reviewer)
                        .reviewee(reviewee)
                        .type(req.getType())
                        .build());

        review.setRating(req.getRating());
        review.setComment(req.getComment());
        review.setAnonymous(Boolean.TRUE.equals(req.getAnonymous()));
        Review saved = reviewRepository.save(review);

        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsForUser(Long userId, ReviewType type) {
        return reviewRepository.findAllByReviewee_IdAndTypeOrderByCreatedAtDesc(userId, type)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ReviewResponseDTO toDto(Review r) {

        boolean anonymous = Boolean.TRUE.equals(r.getAnonymous());

        return ReviewResponseDTO.builder()
                .id(r.getId())
                .reviewerUserId(anonymous ? null : r.getReviewer().getId())
                .reviewerName(anonymous ? "Anonymous" : r.getReviewer().getName())
                .reviewerRole(anonymous ? null : r.getReviewer().getRole().name())
                .revieweeUserId(r.getReviewee().getId())
                .type(r.getType())
                .rating(r.getRating())
                .comment(r.getComment())
                .anonymous(anonymous)
                .createdAt(r.getCreatedAt().toString())
                .updatedAt(r.getUpdatedAt().toString())
                .build();
    }

    @Transactional(readOnly = true)
    public ReviewSummaryDTO getReviewSummaryForUser(Long userId, ReviewType type) {

        Double avg = reviewRepository.getAverageRating(userId, type);
        Long count = reviewRepository.getReviewCount(userId, type);

        List<ReviewResponseDTO> reviews = reviewRepository
                .findAllByReviewee_IdAndTypeOrderByCreatedAtDesc(userId, type)
                .stream()
                .map(this::toDto)
                .toList();

        return ReviewSummaryDTO.builder()
                .averageRating(avg == null ? 0.0 : avg)
                .totalReviews(count == null ? 0L : count)
                .reviews(reviews)
                .build();
    }
    @Transactional(readOnly = true)
    public ReviewSummaryPageDTO getReviewSummaryForUserPaged(Long userId, ReviewType type, int page, int size) {

        Double avg = reviewRepository.getAverageRating(userId, type);
        Long count = reviewRepository.getReviewCount(userId, type);

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewPage = reviewRepository.findAllByReviewee_IdAndType(userId, type, pageable);

        List<ReviewResponseDTO> reviews = reviewPage.getContent()
                .stream()
                .map(this::toDto)
                .toList();

        return ReviewSummaryPageDTO.builder()
                .averageRating(avg == null ? 0.0 : avg)
                .totalReviews(count == null ? 0L : count)
                .page(reviewPage.getNumber())
                .size(reviewPage.getSize())
                .totalPages((long) reviewPage.getTotalPages())
                .totalElements(reviewPage.getTotalElements())
                .reviews(reviews)
                .build();
    }
}
