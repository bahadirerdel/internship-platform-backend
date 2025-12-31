package com.internshipplatform.internshipplatform.repository;

import com.internshipplatform.internshipplatform.entity.Internship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long>, JpaSpecificationExecutor<Internship> {

    List<Internship> findByCompany_Id(Long companyId);

    List<Internship> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    List<Internship> findAllByVisibilityStatus(Internship.InternshipVisibility status);

    Page<Internship> findAllByVisibilityStatus(Internship.InternshipVisibility status, Pageable pageable);


    @Query("""
    SELECT i
    FROM Internship i
    WHERE i.visibilityStatus = :visibility
    AND ( :keywordPattern IS NULL
          OR LOWER(i.title)          LIKE :keywordPattern
          OR LOWER(i.description)    LIKE :keywordPattern
          OR LOWER(i.requiredSkills) LIKE :keywordPattern
        )
    AND ( :locationLower IS NULL OR LOWER(i.location) = :locationLower )
    AND ( :typeLower IS NULL OR LOWER(i.internshipType) = :typeLower )
    AND ( :skillPattern IS NULL OR LOWER(i.requiredSkills) LIKE :skillPattern )
    AND ( :deadlineFrom IS NULL OR i.applicationDeadline >= :deadlineFrom )
    AND ( :deadlineTo IS NULL OR i.applicationDeadline <= :deadlineTo )
    """)
    Page<Internship> searchInternships(
            @Param("visibility") Internship.InternshipVisibility visibility,
            @Param("keywordPattern") String keywordPattern,
            @Param("locationLower")  String locationLower,
            @Param("typeLower")      String typeLower,
            @Param("skillPattern")   String skillPattern,
            @Param("deadlineFrom")   LocalDate deadlineFrom,
            @Param("deadlineTo")     LocalDate deadlineTo,
            Pageable pageable
    );

}
