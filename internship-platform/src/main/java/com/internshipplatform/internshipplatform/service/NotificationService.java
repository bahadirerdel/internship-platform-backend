package com.internshipplatform.internshipplatform.service;

import com.internshipplatform.internshipplatform.dto.NotificationDTO;
import com.internshipplatform.internshipplatform.entity.Notification;
import com.internshipplatform.internshipplatform.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationDTO> getMyNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(n -> NotificationDTO.builder()
                        .id(n.getId())
                        .message(n.getMessage())
                        .read(n.isRead())
                        .createdAt(n.getCreatedAt().toString())
                        .build())
                .toList();
    }

    public void notifyUser(Long userId, String message) {
        notificationRepository.save(Notification.builder()
                .userId(userId)
                .message(message)
                .build());
    }

}
