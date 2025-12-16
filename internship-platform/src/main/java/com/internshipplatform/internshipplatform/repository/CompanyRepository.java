package com.internshipplatform.internshipplatform.repository;

import com.internshipplatform.internshipplatform.entity.Company;
import com.internshipplatform.internshipplatform.entity.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findAllByVerificationStatus(VerificationStatus status);
    Optional<Company> findByUserId(Long userId);
}
