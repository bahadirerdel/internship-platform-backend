package com.internshipplatform.internshipplatform.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyProfileDTO {

    private Long id;
    private Long userId;

    private String name;
    private String industry;
    private String website;
    private String location;
    private String size;
    private String description;
    private String logoUrl;
}
