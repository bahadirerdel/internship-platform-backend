package com.internshipplatform.internshipplatform.repository;

import com.internshipplatform.internshipplatform.entity.ApplicationStatus;
import com.internshipplatform.internshipplatform.entity.InternshipApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InternshipApplicationRepository extends JpaRepository<InternshipApplication, Long> {

    Optional<InternshipApplication> findByStudentIdAndInternshipId(Long studentId, Long internshipId);

    Optional<InternshipApplication> findByInternshipIdAndStudentId(Long internshipId, Long studentId);

    List<InternshipApplication> findByStudentId(Long studentId);

    List<InternshipApplication> findByInternshipId(Long internshipId);

    Optional<InternshipApplication> findFirstByStudent_IdAndInternship_Company_IdAndStatus(
            Long studentUserId,
            Long companyUserId,
            ApplicationStatus status
    );
}
