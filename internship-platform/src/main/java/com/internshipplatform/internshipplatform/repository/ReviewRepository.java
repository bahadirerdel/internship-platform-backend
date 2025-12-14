package com.internshipplatform.internshipplatform.repository;

import com.internshipplatform.internshipplatform.entity.Review;
import com.internshipplatform.internshipplatform.entity.ReviewType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByReviewee_IdAndTypeOrderByCreatedAtDesc(Long revieweeUserId, ReviewType type);

    Optional<Review> findByReviewer_IdAndReviewee_IdAndType(Long reviewerUserId, Long revieweeUserId, ReviewType type);

    @Query("select avg(r.rating) from Review r where r.reviewee.id = :userId and r.type = :type")
    Double getAverageRating(Long userId, ReviewType type);

    @Query("select count(r) from Review r where r.reviewee.id = :userId and r.type = :type")
    Long getReviewCount(Long userId, ReviewType type);

    boolean existsByReviewer_IdAndReviewee_IdAndType(Long reviewerUserId, Long revieweeUserId, ReviewType type);
    Page<Review> findAllByReviewee_IdAndType(Long revieweeUserId, ReviewType type, Pageable pageable);
}
