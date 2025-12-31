package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.AdminActionResponse;
import com.internshipplatform.internshipplatform.dto.AdminUserDTO;
import com.internshipplatform.internshipplatform.entity.Role;
import com.internshipplatform.internshipplatform.entity.User;
import com.internshipplatform.internshipplatform.exception.ForbiddenException;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.mapper.AdminUserMapper;
import com.internshipplatform.internshipplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    @Transactional
    public AdminActionResponse blockUser(Long targetUserId, Long adminUserId, String reason) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == Role.ADMIN) throw new ForbiddenException("Cannot block an admin");
        if (!user.isEnabled()) throw new ForbiddenException("User already blocked");

        user.setEnabled(false);
        user.setBlockedReason(reason);
        user.setBlockedAt(Instant.now());
        user.setBlockedByAdminUserId(adminUserId);

        userRepository.save(user);

        return AdminActionResponse.builder()
                .message("User blocked")
                .adminUserId(adminUserId)
                .at(user.getBlockedAt())
                .build();
    }

    @Transactional
    public AdminActionResponse unblockUser(Long targetUserId, Long adminUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEnabled()) throw new ForbiddenException("User is not blocked");

        user.setEnabled(true);
        user.setBlockedReason(null);
        user.setBlockedAt(null);
        user.setBlockedByAdminUserId(null);

        userRepository.save(user);

        return AdminActionResponse.builder()
                .message("User unblocked")
                .adminUserId(adminUserId)
                .at(Instant.now())
                .build();
    }


    public Page<AdminUserDTO> listUsers(String role, Boolean blockedOnly, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(
                page, size,
                sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending()
        );

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();

            if (role != null && !role.isBlank()) {
                preds.add(cb.equal(cb.upper(root.get("role")), role.toUpperCase()));
            }
            if (blockedOnly != null) {
                // blockedOnly=true -> enabled=false
                preds.add(cb.equal(root.get("enabled"), !blockedOnly));
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };

        return userRepository.findAll(spec, pageable).map(AdminUserMapper::toDto);
    }



}

