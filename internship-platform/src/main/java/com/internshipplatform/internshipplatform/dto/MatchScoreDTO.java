package com.internshipplatform.internshipplatform.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MatchScoreDTO {
    private int score;                 // 0-100 (after cap)
    private Integer capApplied;        // null if no cap
    private List<String> flags;        // DEGREE_TOO_LOW, EXPERIENCE_TOO_LOW, NO_SKILLS_PROFILE
    private ScoreBreakdownDTO breakdown;

    private List<String> matchedRequiredSkills;
    private List<String> missingRequiredSkills; // e.g. {"requiredSkills":40, "preferredSkills":10, "degree":10...}
}
