package com.internshipplatform.internshipplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileUpdateRequest {

    private String university;
    private String department;

    @Size(max = 500, message = "About section is too long")
    private String bio;

    private String skills;   // e.g. "Java, Spring, React"        // optional
}
