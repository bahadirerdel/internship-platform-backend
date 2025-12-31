package com.internshipplatform.internshipplatform.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HideInternshipRequest {
    @Size(max = 300)
    private String reason;
}

