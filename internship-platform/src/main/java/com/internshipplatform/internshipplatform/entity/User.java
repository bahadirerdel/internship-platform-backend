package com.internshipplatform.internshipplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {

    @Id // Primary Key Markdown
    @GeneratedValue(strategy = GenerationType.IDENTITY) // How should DB generate ID
    private Long id;

    @Column(nullable = false, unique = true) // email can't be null and has to be unique
    private String email;

    @JsonIgnore // do not include in API response.
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "blocked_reason", length = 2000)
    private String blockedReason;

    @Column(name = "blocked_at")
    private Instant blockedAt;

    @Column(name = "blocked_by_admin_user_id")
    private Long blockedByAdminUserId;

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

}
