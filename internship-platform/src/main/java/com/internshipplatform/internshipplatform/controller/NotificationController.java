package com.internshipplatform.internshipplatform.controller;

import com.internshipplatform.internshipplatform.dto.NotificationDTO;
import com.internshipplatform.internshipplatform.security.JwtUtil;
import com.internshipplatform.internshipplatform.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasAnyRole('STUDENT','COMPANY','ADMIN')")
    @GetMapping("/my")
    public List<NotificationDTO> getMyNotifications(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        return notificationService.getMyNotifications(userId);
    }
}
