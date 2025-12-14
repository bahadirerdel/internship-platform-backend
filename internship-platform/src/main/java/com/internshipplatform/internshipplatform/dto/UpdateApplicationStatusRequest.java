package com.internshipplatform.internshipplatform.dto;

import com.internshipplatform.internshipplatform.entity.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateApplicationStatusRequest {
    private ApplicationStatus status;
}
