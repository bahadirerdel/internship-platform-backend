package com.internshipplatform.internshipplatform.repository;

import com.internshipplatform.internshipplatform.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    Optional<Interview> findByApplication_Id(Long applicationId);
    boolean existsByApplication_Id(Long applicationId);
    List<Interview> findAllByApplication_Student_IdOrderByScheduledAtAsc(Long studentUserId);

    List<Interview> findAllByApplication_Internship_Company_IdOrderByScheduledAtAsc(Long companyUserId);
}
