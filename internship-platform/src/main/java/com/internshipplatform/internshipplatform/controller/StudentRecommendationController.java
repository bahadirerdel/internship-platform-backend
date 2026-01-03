package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.InternshipMatchItemDTO;
import com.internshipplatform.internshipplatform.dto.InternshipRecommendationDTO;
import com.internshipplatform.internshipplatform.security.JwtUtil;
import com.internshipplatform.internshipplatform.service.StudentRecommendationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students/me")
@RequiredArgsConstructor
public class StudentRecommendationController {

    private final StudentRecommendationService recommendationService;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/recommendations")
    public ResponseEntity<List<InternshipMatchItemDTO>> getRecommendations(
            @RequestParam(defaultValue = "20") int limit,
            HttpServletRequest request
    ) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return ResponseEntity.ok(recommendationService.recommendForStudent(userId, limit));
    }
}
