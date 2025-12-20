package com.internshipplatform.internshipplatform.repository;

import com.internshipplatform.internshipplatform.entity.Role;
import com.internshipplatform.internshipplatform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/****
 * Gives you ready-made DB methods:
 * findAll(), save(), deleteById(), findById(), etc.
 * findByEmail will automatically generate a query like
 * SELECT * FROM users WHERE email = ?
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    Page<User> findAllByEnabledFalse(Pageable pageable);
    Page<User> findAllByRole(Role role, Pageable pageable);
    Page<User> findAllByRoleAndEnabledFalse(Role role, Pageable pageable);
    Page<User> findAllByEnabled(boolean enabled, Pageable pageable);
    Page<User> findAllByRoleAndEnabled(Role role, boolean enabled, Pageable pageable);

}
