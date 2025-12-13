package com.internshipplatform.internshipplatform.repository;

import com.internshipplatform.internshipplatform.entity.SavedInternship;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedInternshipRepository extends JpaRepository<SavedInternship, Long> {

    Optional<SavedInternship> findByStudentIdAndInternshipId(Long studentId, Long internshipId);

    List<SavedInternship> findByStudentId(Long studentId);
}
