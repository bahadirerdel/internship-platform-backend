package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.AdminActionResponse;
import com.internshipplatform.internshipplatform.dto.AdminInternshipRowDTO;
import com.internshipplatform.internshipplatform.entity.Internship;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.repository.InternshipRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminInternshipService {

    private final InternshipRepository internshipRepository;

    @Transactional
    public AdminActionResponse hideInternship(Long internshipId, Long adminUserId, String reason) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

        internship.setVisibilityStatus(Internship.InternshipVisibility.HIDDEN);
        internship.setHiddenReason(reason);
        internship.setHiddenAt(Instant.now());
        internship.setHiddenByAdminUserId(adminUserId);

        internshipRepository.save(internship);

        return AdminActionResponse.builder()
                .message("Internship hidden")
                .adminUserId(adminUserId)
                .at(internship.getHiddenAt())
                .build();
    }

    @Transactional
    public AdminActionResponse unhideInternship(Long internshipId, Long adminUserId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

        internship.setVisibilityStatus(Internship.InternshipVisibility.PUBLIC);
        internship.setHiddenReason(null);
        internship.setHiddenAt(null);
        internship.setHiddenByAdminUserId(null);

        internshipRepository.save(internship);

        return AdminActionResponse.builder()
                .message("Internship unhidden")
                .adminUserId(adminUserId)
                .at(Instant.now()) // action timestamp
                .build();
    }

    @Transactional(readOnly = true)
    public Page<AdminInternshipRowDTO> listInternshipsForAdmin(
            String visibility,
            String q,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                sortDir.equalsIgnoreCase("desc")
                        ? Sort.by(sortBy).descending()
                        : Sort.by(sortBy).ascending()
        );

        Specification<Internship> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> preds = new ArrayList<>();

            if (visibility != null && !visibility.isBlank()) {
                preds.add(cb.equal(
                        root.get("visibilityStatus"),
                        Internship.InternshipVisibility.valueOf(visibility.toUpperCase())
                ));
            }

            if (q != null && !q.isBlank()) {
                String like = "%" + q.toLowerCase() + "%";

                var companyJoin = root.join("company", JoinType.LEFT);

                preds.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("location")), like),
                        cb.like(cb.lower(companyJoin.get("email")), like)
                ));
            }

            return cb.and(preds.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return internshipRepository.findAll(spec, pageable)
                .map(i -> AdminInternshipRowDTO.builder()
                        .id(i.getId())
                        .title(i.getTitle())
                        .companyEmail(i.getCompany().getEmail())
                        .location(i.getLocation())
                        .visibilityStatus(i.getVisibilityStatus().name())
                        .hiddenReason(i.getHiddenReason())
                        .hiddenAt(i.getHiddenAt())
                        .hiddenByAdminUserId(i.getHiddenByAdminUserId())
                        .createdAt(i.getCreatedAt())
                        .build());
    }


}
