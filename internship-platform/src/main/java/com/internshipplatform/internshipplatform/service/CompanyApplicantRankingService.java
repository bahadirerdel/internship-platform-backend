package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.CompanyApplicantMatchDTO;
import com.internshipplatform.internshipplatform.entity.Internship;
import com.internshipplatform.internshipplatform.entity.Student;
import com.internshipplatform.internshipplatform.exception.ForbiddenException;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.repository.InternshipApplicationRepository;
import com.internshipplatform.internshipplatform.repository.InternshipRepository;
import com.internshipplatform.internshipplatform.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyApplicantRankingService {

    private final InternshipRepository internshipRepository;
    private final InternshipApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final ScoringService scoringService;

    public List<CompanyApplicantMatchDTO> listRankedApplicants(Long companyUserId, Long internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

        // ownership check
        if (!internship.getCompany().getId().equals(companyUserId)) {
            throw new ForbiddenException("You don't own this internship");
        }

        var apps = applicationRepository.findAllByInternshipIdOrderByAppliedAtDesc(internshipId);

        return apps.stream().map(app -> {
                    Long studentUserId = app.getStudent().getId(); // student is a User in your model
                    Student student = studentRepository.findByUser_Id(studentUserId)
                            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

                    var match = scoringService.score(student, internship);

                    return CompanyApplicantMatchDTO.builder()
                            .applicationId(app.getId())
                            .studentUserId(studentUserId)
                            .studentName(app.getStudent().getName())
                            .appliedAt(app.getAppliedAt())
                            .university(student.getUniversity())
                            .department(student.getDepartment())
                            .degreeLevel(student.getDegreeLevel() != null ? student.getDegreeLevel().name() : null)
                            .experienceLevel(student.getExperienceLevel() != null ? student.getExperienceLevel().name() : null)
                            .match(match)
                            .build();
                }).sorted((a,b) -> Integer.compare(b.getMatch().getScore(), a.getMatch().getScore()))
                .toList();
    }
}
