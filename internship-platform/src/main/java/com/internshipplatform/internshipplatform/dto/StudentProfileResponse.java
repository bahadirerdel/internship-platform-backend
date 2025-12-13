package com.internshipplatform.internshipplatform.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileResponse {

    private Long id;        // student id
    private Long userId;    // linked user id
    private String email;
    private String name;
    private String role;
    private String skills;
    private String university;
    private String department;
    private Integer graduationYear;
    private String resumeUrl;
    private String bio;
}
