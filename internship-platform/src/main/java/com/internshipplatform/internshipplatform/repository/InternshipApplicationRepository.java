package com.internshipplatform.internshipplatform.repository;

import com.internshipplatform.internshipplatform.entity.ApplicationStatus;
import com.internshipplatform.internshipplatform.entity.InternshipApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
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
    @Query("""
        select a
        from InternshipApplication a
        join a.internship i
        where i.company.id = :companyUserId
        order by a.appliedAt desc
    """)
    List<InternshipApplication> findAllByCompanyUserIdOrderByAppliedAtDesc(
            @Param("companyUserId") Long companyUserId
    );

    @Query("""
        select a
        from InternshipApplication a
        join a.internship i
        where i.company.id = :companyUserId
          and a.appliedAt >= :from
        order by a.appliedAt desc
    """)
    List<InternshipApplication> findAllByCompanyUserIdAndAppliedAtAfterOrderByAppliedAtDesc(
            @Param("companyUserId") Long companyUserId,
            @Param("from") Instant from
    );
    List<InternshipApplication> findAllByInternshipIdOrderByAppliedAtDesc(Long internshipId);

}

