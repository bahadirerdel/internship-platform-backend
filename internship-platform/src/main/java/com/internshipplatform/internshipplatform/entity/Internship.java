package com.internshipplatform.internshipplatform.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "required_skills")
    private String requiredSkills;   // <- must be String


    private LocalDate applicationDeadline;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

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
}
