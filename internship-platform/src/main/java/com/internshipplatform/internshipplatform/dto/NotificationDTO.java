package com.internshipplatform.internshipplatform.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationDTO {
    private Long id;
    private String message;
    private boolean read;
    private String createdAt;
}
