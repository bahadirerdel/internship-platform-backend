package com.internshipplatform.internshipplatform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "internships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Internship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which user (company account) owns this internship
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_user_id", nullable = false)
    private User company;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    private String location;          // e.g. "Izmir", "Remote"
    private String internshipType;    // e.g. "REMOTE", "ONSITE", "HYBRID"
    private String salaryRange;       // e.g. "Unpaid", "₺20k–₺30k"
    private String requirements;
    private String responsibilities;

    @Column(name = "required_skills")
    private String requiredSkills;   // <- must be String


    private LocalDate applicationDeadline;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public enum InternshipVisibility {
        PUBLIC,
        HIDDEN
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility_status", nullable = false)
    @Builder.Default
    private InternshipVisibility visibilityStatus = InternshipVisibility.PUBLIC;

    @Column(name = "hidden_reason", length = 2000)
    private String hiddenReason;

    @Column(name = "hidden_at")
    private Instant hiddenAt;

    @Column(name = "hidden_by_admin_user_id")
    private Long hiddenByAdminUserId;

}
