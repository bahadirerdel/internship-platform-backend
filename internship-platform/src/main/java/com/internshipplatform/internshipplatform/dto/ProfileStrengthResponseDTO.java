package com.internshipplatform.internshipplatform.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileStrengthResponseDTO {
    private int score;
    private List<String> missingFields;
}
