package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.AdminUserDTO;
import com.internshipplatform.internshipplatform.entity.Role;
import com.internshipplatform.internshipplatform.entity.User;
import com.internshipplatform.internshipplatform.exception.ForbiddenException;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.mapper.AdminUserMapper;
import com.internshipplatform.internshipplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    @Transactional
    public void blockUser(Long targetUserId, Long adminUserId, String reason) {

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new ForbiddenException("Cannot block an admin");
        }

        if (!user.isEnabled()) {
            throw new ForbiddenException("User already blocked");
        }

        user.setEnabled(false);
        user.setBlockedReason(reason);
        user.setBlockedAt(Instant.now());
        user.setBlockedByAdminUserId(adminUserId);

        userRepository.save(user);
    }

    @Transactional
    public void unblockUser(Long targetUserId) {

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEnabled()) {
            throw new ForbiddenException("User is not blocked");
        }

        user.setEnabled(true);
        user.setBlockedReason(null);
        user.setBlockedAt(null);
        user.setBlockedByAdminUserId(null);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<AdminUserDTO> listUsers(
            String role,
            Boolean blockedOnly,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        Pageable pageable = PageRequest.of(
                page, size,
                sortDir.equalsIgnoreCase("desc")
                        ? Sort.by(sortBy).descending()
                        : Sort.by(sortBy).ascending()
        );

        Page<User> users = userRepository.findAll(pageable);

        Stream<User> stream = users.getContent().stream();

        if (role != null && !role.isBlank()) {
            stream = stream.filter(u -> u.getRole().name().equalsIgnoreCase(role));
        }

        if (blockedOnly != null) {
            // blockedOnly=true  -> enabled=false
            // blockedOnly=false -> enabled=true
            stream = stream.filter(u -> u.isEnabled() != blockedOnly);
        }

        List<AdminUserDTO> dtos = stream.map(AdminUserMapper::toDto).toList();

        return new PageImpl<>(dtos, pageable, users.getTotalElements());
    }


}

