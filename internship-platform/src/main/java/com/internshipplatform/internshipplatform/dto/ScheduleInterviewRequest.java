package com.internshipplatform.internshipplatform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleInterviewRequest {
    // ISO-8601 string from frontend/Postman, example: "2025-12-20T14:00:00Z"
    private String scheduledAt;
    private String meetingLink; // optional
}
