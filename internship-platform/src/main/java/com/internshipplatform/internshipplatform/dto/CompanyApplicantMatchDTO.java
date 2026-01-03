package com.internshipplatform.internshipplatform.dto;

import com.internshipplatform.internshipplatform.dto.MatchScoreDTO;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CompanyApplicantMatchDTO {
    private Long applicationId;

    private Long studentUserId;
    private String studentName;
    private String university;
    private String department;

    private String degreeLevel;        // enum name or null
    private String experienceLevel;    // enum name or null

    private MatchScoreDTO match;
}
