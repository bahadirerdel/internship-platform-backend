package com.internshipplatform.internshipplatform.repository;

import com.internshipplatform.internshipplatform.entity.ApplicationStatus;
import com.internshipplatform.internshipplatform.entity.InternshipApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InternshipApplicationRepository extends JpaRepository<InternshipApplication, Long> {

    Optional<InternshipApplication> findByStudent_IdAndInternship_Id(Long studentUserId, Long internshipId);

    List<InternshipApplication> findByStudent_Id(Long studentUserId);

    List<InternshipApplication> findByInternship_Id(Long internshipId);

    // ✅ Company interns: all accepted apps for internships owned by this company user
    List<InternshipApplication> findByInternship_Company_IdAndStatus(Long companyUserId, ApplicationStatus status);

    // ✅ Optional: only accepted app between a student and company
    Optional<InternshipApplication> findFirstByStudent_IdAndInternship_Company_IdAndStatus(
            Long studentUserId,
            Long companyUserId,
            ApplicationStatus status
    );
}
