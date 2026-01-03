package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.CompanyApplicantMatchDTO;
import com.internshipplatform.internshipplatform.dto.MatchScoreDTO;
import com.internshipplatform.internshipplatform.entity.Internship;
import com.internshipplatform.internshipplatform.entity.Student;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.repository.InternshipRepository;
import com.internshipplatform.internshipplatform.repository.StudentRepository;
import com.internshipplatform.internshipplatform.security.JwtUtil;
import com.internshipplatform.internshipplatform.service.CompanyApplicantRankingService;
import com.internshipplatform.internshipplatform.service.ScoringService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies/me")
@RequiredArgsConstructor
public class CompanyApplicantRankingController {

    private final CompanyApplicantRankingService companyApplicantRankingService;
    private final StudentRepository studentRepository;
    private final InternshipRepository internshipRepository;
    private final ScoringService scoringService;
    private final JwtUtil jwtUtil;

    // ✅ GET /api/companies/me/internships/{internshipId}/applicants-ranked
    @PreAuthorize("hasRole('COMPANY')")
    @GetMapping("/internships/{internshipId}/applicants-ranked")
    public List<CompanyApplicantMatchDTO> rankedApplicants(
            @PathVariable Long internshipId,
            HttpServletRequest request
    ) {
        Long companyUserId = jwtUtil.getUserIdFromRequest(request);
        return companyApplicantRankingService.listRankedApplicants(companyUserId, internshipId);
    }

    // ✅ GET /api/companies/me/internships/{internshipId}/match/student/{studentUserId}
    // Explainability endpoint (optional but SUPER useful)
    @PreAuthorize("hasRole('COMPANY')")
    @GetMapping("/internships/{internshipId}/match/student/{studentUserId}")
    public MatchScoreDTO explainMatch(
            @PathVariable Long internshipId,
            @PathVariable Long studentUserId,
            HttpServletRequest request
    ) {
        Long companyUserId = jwtUtil.getUserIdFromRequest(request);

        Internship it = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

        // ✅ Ownership check: company can only explain matches for its own internship
        if (it.getCompany() == null || !it.getCompany().getId().equals(companyUserId)) {
            throw new com.internshipplatform.internshipplatform.exception.ForbiddenException(
                    "You can only view matches for your own internship."
            );
        }

        Student s = studentRepository.findByUser_Id(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return scoringService.score(s, it);
    }
}
