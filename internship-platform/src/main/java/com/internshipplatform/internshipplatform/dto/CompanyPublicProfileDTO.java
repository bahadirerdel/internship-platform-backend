package com.internshipplatform.internshipplatform.dto;

import com.internshipplatform.internshipplatform.entity.VerificationStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyPublicProfileDTO {
    private Long userId;
    private String name;
    private String industry;
    private String website;
    private String location;
    private String size;
    private String description;
    private String logoUrl;

    private VerificationStatus verificationStatus;
    private Boolean verified;
}

