package com.internshipplatform.internshipplatform.service;
import io.jsonwebtoken.Claims;
import com.internshipplatform.internshipplatform.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    private static final String SECRET = "this_is_a_very_secret_key_for_jwt_123456";
    private final Key signingKey = Keys.hmacShaKeyFor(SECRET.getBytes());

    // -------------------------------
    // 1. Generate JWT
    // -------------------------------
    public String generateToken(User user) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())   // store role as string
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // -------------------------------
    // 2. Extract email (or subject)
    // -------------------------------
    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    // -------------------------------
    // 3. Extract Role from JWT
    // -------------------------------
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // 🔹 NEW: read userId (sub) from token
    public Long extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }


    // -------------------------------
    // 5. Validate token (later used in filters)
    // -------------------------------
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);   // will throw if invalid
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // -------------------------------
    // 🔥 Core method: get claims
    // -------------------------------
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
