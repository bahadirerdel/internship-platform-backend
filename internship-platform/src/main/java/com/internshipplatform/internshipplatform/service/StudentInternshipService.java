package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.ApplicationResponseDTO;
import com.internshipplatform.internshipplatform.dto.InternshipResponseDTO;
import com.internshipplatform.internshipplatform.entity.*;
import com.internshipplatform.internshipplatform.exception.ForbiddenException;
import com.internshipplatform.internshipplatform.mapper.InternshipMapper;
import com.internshipplatform.internshipplatform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.exception.BadRequestException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentInternshipService {

    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;
    private final SavedInternshipRepository savedInternshipRepository;
    private final InternshipApplicationRepository applicationRepository;
    private final InternshipMapper internshipMapper;

    private User getStudentOrThrow(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() != Role.STUDENT) {
            throw new ForbiddenException("Only students can perform this action.");
        }
        return user;
    }

    // ---------- SAVED INTERNSHIPS ----------

    public void saveInternship(Long internshipId, Long studentId) {
        User student = getStudentOrThrow(studentId);

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        boolean alreadySaved = savedInternshipRepository
                .findByStudentIdAndInternshipId(student.getId(), internship.getId())
                .isPresent();

        if (alreadySaved) {
            // silently ignore, or throw error if you prefer
            return;
        }

        SavedInternship saved = SavedInternship.builder()
                .student(student)
                .internship(internship)
                .build();

        savedInternshipRepository.save(saved);
    }

    public void unsaveInternship(Long internshipId, Long studentId) {
        User student = getStudentOrThrow(studentId);

        SavedInternship saved = savedInternshipRepository
                .findByStudentIdAndInternshipId(student.getId(), internshipId)
                .orElseThrow(() -> new RuntimeException("Saved internship not found"));

        savedInternshipRepository.delete(saved);
    }

    public List<InternshipResponseDTO> getMySavedInternships(Long studentId) {
        User student = getStudentOrThrow(studentId);

        List<SavedInternship> saved = savedInternshipRepository.findByStudentId(student.getId());

        return saved.stream()
                .map(SavedInternship::getInternship)
                .map(internshipMapper::toResponseDTO)
                .toList();
    }

    // ---------- APPLICATIONS ----------

    public void applyToInternship(Long internshipId, Long studentId) {
        User student = getStudentOrThrow(studentId);

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        boolean alreadyApplied = applicationRepository
                .findByStudentIdAndInternshipId(student.getId(), internship.getId())
                .isPresent();

        if (alreadyApplied) {
            throw new RuntimeException("You already applied to this internship.");
        }

        InternshipApplication app = InternshipApplication.builder()
                .student(student)
                .internship(internship)
                .status(ApplicationStatus.PENDING)
                .build();

        applicationRepository.save(app);
    }

    public List<ApplicationResponseDTO> getMyApplications(Long studentId) {
        User student = getStudentOrThrow(studentId);

        return applicationRepository.findByStudentId(student.getId())
                .stream()
                .map(app -> ApplicationResponseDTO.builder()
                        .applicationId(app.getId())
                        .status(app.getStatus())
                        .appliedAt(app.getAppliedAt())
                        .internship(internshipMapper.toResponseDTO(app.getInternship()))
                        .build()
                )
                .toList();
    }

    // For companies: see applicants of a given internship
    public List<ApplicationResponseDTO> getApplicationsForInternship(Long internshipId, Long companyUserId) {
        // Check internship and ownership
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

        if (!internship.getCompany().getId().equals(companyUserId)) {
            throw new ForbiddenException("You do not have permission to view applicants for this internship.");
        }

        return applicationRepository.findByInternshipId(internshipId)
                .stream()
                .map(app -> {
                    User s = app.getStudent();
                    return ApplicationResponseDTO.builder()
                            .applicationId(app.getId())
                            .status(app.getStatus())
                            .appliedAt(app.getAppliedAt())
                            .studentId(s.getId())
                            .studentEmail(s.getEmail())
                            .studentName(s.getName())
                            .build();
                })
                .toList();
    }
    public void withdrawApplication(Long internshipId, Long studentId) {
        InternshipApplication app = applicationRepository
                .findByInternshipIdAndStudentId(internshipId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        // Optional: Only allow withdraw while pending
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("You can only withdraw PENDING applications.");
        }

        applicationRepository.delete(app);
    }

}
