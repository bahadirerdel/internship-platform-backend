package com.internshipplatform.internshipplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "reviews",
        uniqueConstraints = @UniqueConstraint(columnNames = {"reviewer_user_id", "reviewee_user_id", "type"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reviewer_user_id", nullable = false)
    private User reviewer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reviewee_user_id", nullable = false)
    private User reviewee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewType type; // COMPANY_REVIEW or STUDENT_REVIEW

    @Column(nullable = false)
    private Integer rating; // 1..5

    @Column(length = 2000)
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
    @Column(nullable = false)
    private Boolean anonymous = false;

}
