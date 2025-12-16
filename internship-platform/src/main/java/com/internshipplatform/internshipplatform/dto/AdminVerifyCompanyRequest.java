package com.internshipplatform.internshipplatform.dto;

import com.internshipplatform.internshipplatform.entity.VerificationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminVerifyCompanyRequest {
    private VerificationStatus status; // APPROVED or REJECTED
    private String note; // optional reason
}
