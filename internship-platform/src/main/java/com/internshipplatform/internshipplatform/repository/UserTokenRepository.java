package com.internshipplatform.internshipplatform.repository;

import com.internshipplatform.internshipplatform.entity.TokenType;
import com.internshipplatform.internshipplatform.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Optional<UserToken> findByTokenAndType(String token, TokenType type);

    void deleteByUserIdAndType(Long userId, TokenType type);
}
