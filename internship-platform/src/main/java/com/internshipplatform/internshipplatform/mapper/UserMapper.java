package com.internshipplatform.internshipplatform.mapper;

import com.internshipplatform.internshipplatform.dto.UserResponseDTO;
import com.internshipplatform.internshipplatform.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class UserMapper {

    public UserResponseDTO toResponseDto(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())   // enum → String for response
                .build();
    }

    public List<UserResponseDTO> toResponseDtoList(List<User> users) {
        return users.stream()
                .map(this::toResponseDto)
                .toList();
    }
}


