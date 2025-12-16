package com.internshipplatform.internshipplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // link to User – for now just store userId like Student does
    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String name;          // company name (can default to user name or email)

    private String industry;      // e.g. "Software", "Finance"
    private String website;       // e.g. "https://mycompany.com"
    private String location;      // e.g. "Izmir, Turkey"
    private String size;          // e.g. "11-50", "51-200"
    private String description;   // about the company
    private String logoUrl;       // future: logo image

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

    @Column(name = "verification_requested_at")
    private Instant verificationRequestedAt;

    @Column(name = "verification_reviewed_at")
    private Instant verificationReviewedAt;

    @Column(name = "verification_note", length = 2000)
    private String verificationNote;

}
