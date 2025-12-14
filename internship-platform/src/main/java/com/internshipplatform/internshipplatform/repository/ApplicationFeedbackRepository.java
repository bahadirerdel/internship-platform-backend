package com.internshipplatform.internshipplatform.repository;

import com.internshipplatform.internshipplatform.entity.ApplicationFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationFeedbackRepository extends JpaRepository<ApplicationFeedback, Long> {
    Optional<ApplicationFeedback> findByApplication_Id(Long applicationId);
}
