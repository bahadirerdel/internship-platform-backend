package com.internshipplatform.internshipplatform.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipRecommendationDTO {
    private InternshipResponseDTO internship;
    private MatchScoreDTO match;
}
