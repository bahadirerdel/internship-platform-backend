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

    // ✅ correct: /api/companies/me/internships/{id}/applicants-ranked
    @PreAuthorize("hasRole('COMPANY')")
    @GetMapping("/internships/{internshipId}/applicants-ranked")
    public List<CompanyApplicantMatchDTO> rankedApplicants(
            HttpServletRequest request,
            @PathVariable Long internshipId
    ) {
        Long companyUserId = jwtUtil.getUserIdFromRequest(request);
        return companyApplicantRankingService.listRankedApplicants(companyUserId, internshipId);
    }

    // ✅ correct: /api/companies/me/match/student/{studentUserId}/internship/{internshipId}
    @PreAuthorize("hasRole('COMPANY')")
    @GetMapping("/match/student/{studentUserId}/internship/{internshipId}")
    public MatchScoreDTO explainMatch(
            HttpServletRequest request,
            @PathVariable Long studentUserId,
            @PathVariable Long internshipId
    ) {
        // (optional) company authorization checks can be inside service if you want
        Student s = studentRepository.findByUser_Id(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Internship it = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

        return scoringService.score(s, it);
    }
}

