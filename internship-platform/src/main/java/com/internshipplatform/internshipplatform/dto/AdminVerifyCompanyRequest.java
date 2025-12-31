package com.internshipplatform.internshipplatform.dto;

import com.internshipplatform.internshipplatform.entity.VerificationStatus;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminVerifyCompanyRequest {
    private VerificationStatus status; // APPROVED or REJECTED
    @Size(max = 300)
    private String note; // optional reason
}
