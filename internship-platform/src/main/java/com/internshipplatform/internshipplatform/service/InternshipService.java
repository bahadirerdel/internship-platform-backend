package com.internshipplatform.internshipplatform.service;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.dto.InternshipRequestDTO;
import com.internshipplatform.internshipplatform.dto.InternshipResponseDTO;
import com.internshipplatform.internshipplatform.dto.InternshipUpdateRequest;
import com.internshipplatform.internshipplatform.entity.Internship;
import com.internshipplatform.internshipplatform.entity.Role;
import com.internshipplatform.internshipplatform.entity.User;
import com.internshipplatform.internshipplatform.mapper.InternshipMapper;
import com.internshipplatform.internshipplatform.repository.InternshipRepository;
import com.internshipplatform.internshipplatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.exception.ForbiddenException;
import com.internshipplatform.internshipplatform.exception.BadRequestException;

@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;
    private final InternshipMapper internshipMapper;

    // Public internship listing
    public List<InternshipResponseDTO> getAllPublicInternships() {
        return internshipRepository.findAll()
                .stream()
                .map(internshipMapper::toResponseDTO)
                .toList();
    }

    // Company → list own internships
    public List<InternshipResponseDTO> getMyCompanyInternships(Long companyUserId) {
        return internshipRepository.findByCompany_Id(companyUserId)
                .stream()
                .map(internshipMapper::toResponseDTO)
                .toList();
    }

    // Company creates internship
    public InternshipResponseDTO createInternship(Long companyUserId, InternshipRequestDTO request) {
        User companyUser = userRepository.findById(companyUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (companyUser.getRole() != Role.COMPANY) {
            throw new ForbiddenException("Only company accounts can create internships");
        }
        Internship internship = internshipMapper.toEntity(request, companyUser);
        Internship saved = internshipRepository.save(internship);

        return internshipMapper.toResponseDTO(saved);
    }

    // Search internships (public)
    public Page<InternshipResponseDTO> searchInternships(
            String keyword,
            String location,
            String type,
            String skill,
            String deadlineFrom,
            String deadlineTo,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        // --- text filters (normalize + lowercase) ---
        String keywordPattern = (keyword == null || keyword.isBlank())
                ? null
                : "%" + keyword.toLowerCase().trim() + "%";

        String locationLower = (location == null || location.isBlank())
                ? null
                : location.toLowerCase().trim();

        String typeLower = (type == null || type.isBlank())
                ? null
                : type.toLowerCase().trim();

        String skillPattern = (skill == null || skill.isBlank())
                ? null
                : "%" + skill.toLowerCase().trim() + "%";

        // --- date filters (parse to LocalDate or keep null) ---
        LocalDate from = null;
        LocalDate to   = null;

        if (deadlineFrom != null && !deadlineFrom.isBlank()) {
            from = LocalDate.parse(deadlineFrom.trim()); // expects 2025-01-01 format
        }
        if (deadlineTo != null && !deadlineTo.isBlank()) {
            to = LocalDate.parse(deadlineTo.trim());
        }

        // --- sorting / paging ---
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Internship> internships = internshipRepository.searchInternships(
                keywordPattern,
                locationLower,
                typeLower,
                skillPattern,
                from,
                to,
                pageable
        );

        return internships.map(internshipMapper::toResponseDTO);
    }





    // Company → list own internships (ordered)
    public List<InternshipResponseDTO> getMyInternships(Long currentUserId) {
        return internshipRepository.findByCompanyIdOrderByCreatedAtDesc(currentUserId)
                .stream()
                .map(internshipMapper::toResponseDTO)
                .toList();
    }

    // Company → get specific internship by id
    public InternshipResponseDTO getMyInternshipById(Long internshipId, Long companyId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (!internship.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("You are not the owner of this internship.");
        }

        return internshipMapper.toResponseDTO(internship);
    }

    // Company → update internship
    @Transactional
    public InternshipResponseDTO updateMyInternship(
            Long internshipId,
            Long companyId,
            InternshipUpdateRequest request
    ) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (!internship.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("You are not the owner of this internship.");
        }

        if (request.getTitle() != null) internship.setTitle(request.getTitle());
        if (request.getDescription() != null) internship.setDescription(request.getDescription());
        if (request.getLocation() != null) internship.setLocation(request.getLocation());
        if (request.getInternshipType() != null) internship.setInternshipType(request.getInternshipType());
        if (request.getSalaryRange() != null) internship.setSalaryRange(request.getSalaryRange());
        if (request.getRequiredSkills() != null) internship.setRequiredSkills(request.getRequiredSkills());
        if (request.getApplicationDeadline() != null) {
            internship.setApplicationDeadline(request.getApplicationDeadline());
        }

        internship.setUpdatedAt(Instant.now());
        Internship saved = internshipRepository.save(internship);
        return internshipMapper.toResponseDTO(saved);
    }

    // Company → delete internship
    @Transactional
    public void deleteMyInternship(Long internshipId, Long companyId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (!internship.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("You are not the owner of this internship.");
        }

        internshipRepository.delete(internship);
    }

    public InternshipResponseDTO getInternshipById(Long internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

        return internshipMapper.toResponseDTO(internship);
    }


}
