package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.InternshipMatchItemDTO;
import com.internshipplatform.internshipplatform.entity.Internship;
import com.internshipplatform.internshipplatform.entity.Student;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.mapper.InternshipMapper;
import com.internshipplatform.internshipplatform.repository.InternshipRepository;
import com.internshipplatform.internshipplatform.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentRecommendationService {

    private final StudentRepository studentRepository;
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final ScoringService scoringService;

    public List<InternshipMatchItemDTO> recommendForStudent(Long userId, int limit) {
        Student student = studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // If you want PUBLIC only, replace findAll() with your visibility repo method later
        List<Internship> internships = internshipRepository.findAll();

        return internships.stream()
                .map(it -> InternshipMatchItemDTO.builder()
                        .internship(internshipMapper.toResponseDTO(it))
                        .match(scoringService.score(student, it)) // MUST return MatchScoreDTO
                        .build()
                )
                .sorted(Comparator.comparingInt((InternshipMatchItemDTO x) -> x.getMatch().getScore()).reversed())
                .limit(Math.max(limit, 0))
                .toList();
    }
}

