package com.internshipplatform.internshipplatform.dto;

import lombok.*;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentPublicProfileDTO {
    private Long userId;
    private String name;

    private String university;
    private String department;
    private Integer graduationYear;

    private String skills; // CSV ok for now
    private String bio;
}

