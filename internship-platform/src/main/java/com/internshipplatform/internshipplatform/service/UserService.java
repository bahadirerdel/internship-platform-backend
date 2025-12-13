package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.UserCreateRequest;
import com.internshipplatform.internshipplatform.dto.UserResponseDTO;
import com.internshipplatform.internshipplatform.entity.User;
import com.internshipplatform.internshipplatform.mapper.UserMapper;
import com.internshipplatform.internshipplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // GET /api/users
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toResponseDtoList(users);
    }

    // POST /api/users
    public UserResponseDTO createUser(UserCreateRequest request) {

        // For now, give them a dummy password so DB is happy.
        // Later we can send a real password or reset link.
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .role(request.getRole())     // already a Role enum
                .password("TEMP_PASSWORD")   // placeholder, or generate random
                .build();

        User saved = userRepository.save(user);
        return userMapper.toResponseDto(saved);
    }

    // (Existing method you already had)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponseDto(user);
    }
}
