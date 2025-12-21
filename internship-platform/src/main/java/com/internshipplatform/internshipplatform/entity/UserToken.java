package com.internshipplatform.internshipplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_tokens")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(nullable=false, unique=true, length=255)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=30)
    private TokenType type;

    @Column(name="expires_at", nullable=false)
    private Instant expiresAt;

    @Column(name="used_at")
    private Instant usedAt;

    @Column(name="created_at", nullable=false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
