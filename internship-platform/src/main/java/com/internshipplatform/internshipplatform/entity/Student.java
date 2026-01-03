package com.internshipplatform.internshipplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1–1 with User (each student has one User account)
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String university;
    private String department;
    private Integer graduationYear;

    @Column(name = "resume_file_name")
    private String resumeFileName;

    @Column(name = "resume_content_type")
    private String resumeContentType;

    @Column(name = "resume_size")
    private Long resumeSize;

    @Column(name = "resume_uploaded_at")
    private Instant resumeUploadedAt;

    @Column(name = "resume_original_file_name")
    private String resumeOriginalFileName;

    private String bio;
    private String skills;

    @Column(name = "core_skills", length = 2000)
    private String coreSkills;

    @Column(name = "other_skills", length = 2000)
    private String otherSkills;

    // Education
    @Enumerated(EnumType.STRING)
    @Column(name = "degree_level")
    private DegreeLevel degreeLevel;

    @Column(name = "gpa")
    private Double gpa;

    // Experience
    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private ExperienceLevel experienceLevel;

    @Column(name = "total_experience_months")
    private Integer totalExperienceMonths;

    // Extras
    @Column(name = "certifications", length = 2000)
    private String certifications;

    @Column(name = "languages", length = 1000)
    private String languages;
}
