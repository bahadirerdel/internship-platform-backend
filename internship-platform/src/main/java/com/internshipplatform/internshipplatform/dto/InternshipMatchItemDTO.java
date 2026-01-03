package com.internshipplatform.internshipplatform.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InternshipMatchItemDTO {
    private InternshipResponseDTO internship;
    private MatchScoreDTO match;
}
