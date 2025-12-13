package com.internshipplatform.internshipplatform.dto;

import com.internshipplatform.internshipplatform.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String email;
    private String role;
    private String name;
}
