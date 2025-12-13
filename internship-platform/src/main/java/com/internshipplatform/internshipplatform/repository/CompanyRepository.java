package com.internshipplatform.internshipplatform.repository;

import com.internshipplatform.internshipplatform.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByUserId(Long userId);
}
