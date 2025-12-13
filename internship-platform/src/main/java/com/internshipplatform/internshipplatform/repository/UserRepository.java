package com.internshipplatform.internshipplatform.repository;

import com.internshipplatform.internshipplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/****
 * Gives you ready-made DB methods:
 * findAll(), save(), deleteById(), findById(), etc.
 * findByEmail will automatically generate a query like
 * SELECT * FROM users WHERE email = ?
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
