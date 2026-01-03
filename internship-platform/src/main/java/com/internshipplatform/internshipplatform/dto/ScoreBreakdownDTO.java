package com.internshipplatform.internshipplatform.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreBreakdownDTO {
    private int requiredSkills;   // 0-50
    private int preferredSkills;  // 0-20
    private int degree;           // 0-10
    private int experience;       // 0-10
    private int extras;           // 0-10
}
