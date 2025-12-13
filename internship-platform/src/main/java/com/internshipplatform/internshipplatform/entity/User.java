package com.internshipplatform.internshipplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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
}
