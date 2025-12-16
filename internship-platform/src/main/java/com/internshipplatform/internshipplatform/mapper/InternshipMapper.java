package com.internshipplatform.internshipplatform.mapper;

import com.internshipplatform.internshipplatform.dto.InternshipRequestDTO;
import com.internshipplatform.internshipplatform.dto.InternshipResponseDTO;
import com.internshipplatform.internshipplatform.entity.Company;
import com.internshipplatform.internshipplatform.entity.Internship;
import com.internshipplatform.internshipplatform.entity.User;
import com.internshipplatform.internshipplatform.entity.VerificationStatus;
import com.internshipplatform.internshipplatform.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InternshipMapper {

    private final CompanyRepository companyRepository;
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

        VerificationStatus verificationStatus =
                internship.getCompany() != null
                        ? companyRepository
                        .findByUserId(internship.getCompany().getId())
                        .map(Company::getVerificationStatus)
                        .orElse(VerificationStatus.UNVERIFIED)
                        : VerificationStatus.UNVERIFIED;

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
                .companyVerificationStatus(verificationStatus.name())
                .companyVerified(verificationStatus == VerificationStatus.APPROVED)
                .build();
    }


    // ✅ This is the one InternshipService is calling
    public List<InternshipResponseDTO> toResponseList(List<Internship> internships) {

        return internships.stream()
                .map(this::toResponseDTO)
                .toList();
    }
}
