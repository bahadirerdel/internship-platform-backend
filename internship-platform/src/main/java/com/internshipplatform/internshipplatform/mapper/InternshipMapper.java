package com.internshipplatform.internshipplatform.mapper;

import com.internshipplatform.internshipplatform.dto.InternshipRequestDTO;
import com.internshipplatform.internshipplatform.dto.InternshipResponseDTO;
import com.internshipplatform.internshipplatform.entity.Internship;
import com.internshipplatform.internshipplatform.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InternshipMapper {

    public Internship toEntity(InternshipRequestDTO dto, User company) {
        if (dto == null) return null;

        return Internship.builder()
                .company(company)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .internshipType(dto.getInternshipType())
                .salaryRange(dto.getSalaryRange())
                .requiredSkills(dto.getRequiredSkills())
                .applicationDeadline(dto.getApplicationDeadline())
                .build();
    }

    public void updateEntity(Internship internship, InternshipRequestDTO dto) {
        // for PUT update – only overwrite fields from DTO
        internship.setTitle(dto.getTitle());
        internship.setDescription(dto.getDescription());
        internship.setLocation(dto.getLocation());
        internship.setInternshipType(dto.getInternshipType());
        internship.setSalaryRange(dto.getSalaryRange());
        internship.setRequiredSkills(dto.getRequiredSkills());
        internship.setApplicationDeadline(dto.getApplicationDeadline());
    }

    public InternshipResponseDTO toResponseDTO(Internship internship) {
        if (internship == null) return null;

        return InternshipResponseDTO.builder()
                .id(internship.getId())
                .title(internship.getTitle())
                .description(internship.getDescription())
                .location(internship.getLocation())
                .internshipType(internship.getInternshipType())
                .salaryRange(internship.getSalaryRange())
                .requiredSkills(internship.getRequiredSkills())
                .applicationDeadline(internship.getApplicationDeadline())
                .companyId(
                        internship.getCompany() != null
                                ? internship.getCompany().getId()
                                : null
                )
                .build();
    }

    // ✅ This is the one InternshipService is calling
    public List<InternshipResponseDTO> toResponseList(List<Internship> internships) {
        return internships.stream()
                .map(this::toResponseDTO)
                .toList();
    }
}
