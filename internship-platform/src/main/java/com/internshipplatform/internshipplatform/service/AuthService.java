package com.internshipplatform.internshipplatform.service;
import com.internshipplatform.internshipplatform.entity.*;
import com.internshipplatform.internshipplatform.exception.ForbiddenException;
import com.internshipplatform.internshipplatform.exception.ResourceNotFoundException;
import com.internshipplatform.internshipplatform.repository.CompanyRepository;
import com.internshipplatform.internshipplatform.repository.StudentRepository;
import com.internshipplatform.internshipplatform.repository.UserTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.internshipplatform.internshipplatform.dto.*;
import com.internshipplatform.internshipplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final UserTokenRepository tokenRepository;
    private final EmailService emailService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(request.getRole())
                .build();

        User saved = userRepository.save(user);

        if (saved.getRole() == Role.STUDENT) {
            Student student = Student.builder()
                    .user(saved) // Student has `User user`
                    .build();
            studentRepository.save(student);
        }

        if (saved.getRole() == Role.COMPANY) {
            Company company = Company.builder()
                    .userId(saved.getId())     // Company stores Long userId
                    .name(saved.getName())
                    .verificationStatus(VerificationStatus.UNVERIFIED)
                    .build();
            companyRepository.save(company);
        }

        sendEmailVerification(saved);

        String jwt = jwtService.generateToken(saved);

        return AuthResponse.builder()
                .message("Registration successful (verification sent)")
                .token(jwt)
                .userId(saved.getId())
                .email(saved.getEmail())
                .role(saved.getRole())
                .name(saved.getName())
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // ✅ Admin blocked user
        if (!user.isEnabled()) {
            throw new RuntimeException("Account is blocked");
            // or: throw new ForbiddenException("Account is blocked");
        }

        // ✅ Email not verified
        if (!user.isEmailVerified()) {
            throw new RuntimeException("Please verify your email first");
            // or: throw new ForbiddenException("Please verify your email first");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .message("Login successful")
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .name(user.getName())
                .build();
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    public void sendEmailVerification(User user) {
        // remove old verify tokens (optional)
        tokenRepository.deleteByUserIdAndType(user.getId(), TokenType.EMAIL_VERIFY);

        String token = generateToken();
        UserToken t = UserToken.builder()
                .userId(user.getId())
                .token(token)
                .type(TokenType.EMAIL_VERIFY)
                .expiresAt(Instant.now().plus(Duration.ofHours(24)))
                .build();
        tokenRepository.save(t);

        String link = "http://localhost:8080/api/auth/verify-email?token=" + token;
        emailService.sendEmail(
                user.getEmail(),
                "Verify your email",
                "Click to verify (demo):\n" + link
        );
    }
    @Transactional
    public void verifyEmail(String token) {
        UserToken t = tokenRepository.findByTokenAndType(token, TokenType.EMAIL_VERIFY)
                .orElseThrow(() -> new ForbiddenException("Invalid verification token"));

        if (t.isUsed()) throw new ForbiddenException("Token already used");
        if (t.isExpired()) throw new ForbiddenException("Token expired");

        User user = userRepository.findById(t.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEmailVerified(true);
        userRepository.save(user);

        t.setUsedAt(Instant.now());
        tokenRepository.save(t);
    }
    public void forgotPassword(String email) {
        // IMPORTANT: do not reveal if user exists
        User user = userRepository.findByEmail(email);
        if (user == null) return;

        tokenRepository.deleteByUserIdAndType(user.getId(), TokenType.PASSWORD_RESET);

        String token = generateToken();
        UserToken t = UserToken.builder()
                .userId(user.getId())
                .token(token)
                .type(TokenType.PASSWORD_RESET)
                .expiresAt(Instant.now().plus(Duration.ofMinutes(30)))
                .build();
        tokenRepository.save(t);

        String link = "http://localhost:8080/api/auth/reset-password?token=" + token;

        emailService.sendEmail(
                user.getEmail(),
                "Reset your password",
                "Use this token (demo): " + token + "\nLink (demo):\n" + link
        );
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        UserToken t = tokenRepository.findByTokenAndType(token, TokenType.PASSWORD_RESET)
                .orElseThrow(() -> new ForbiddenException("Invalid reset token"));

        if (t.isUsed()) throw new ForbiddenException("Token already used");
        if (t.isExpired()) throw new ForbiddenException("Token expired");

        User user = userRepository.findById(t.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        t.setUsedAt(Instant.now());
        tokenRepository.save(t);
    }

}
