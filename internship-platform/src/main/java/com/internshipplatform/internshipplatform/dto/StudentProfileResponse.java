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

    // ✅ Resume fields (add these)
    private String resumeFileName;          // internal stored name (uuid)
    private String resumeOriginalFileName;  // original uploaded file name
    private Long resumeSize;
    private String resumeContentType;

    // Optional convenience
    private String resumeDownloadUrl;

    private String bio;
}
