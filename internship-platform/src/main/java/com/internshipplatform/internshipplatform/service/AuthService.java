package com.internshipplatform.internshipplatform.service;
import com.internshipplatform.internshipplatform.entity.*;
import com.internshipplatform.internshipplatform.repository.CompanyRepository;
import com.internshipplatform.internshipplatform.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.internshipplatform.internshipplatform.dto.*;
import com.internshipplatform.internshipplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("Email already registered");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(hashedPassword)  // store hash instead of plain
                .name(request.getName())
                .role(request.getRole())
                .build();

        User saved = userRepository.save(user);
        if (user.getRole() == Role.STUDENT) {
            Student student = Student.builder()
                    .user(user)   // because your Student entity has `private User user;`
                    .build();
            studentRepository.save(student);
        }

        if (user.getRole() == Role.COMPANY) {
            Company company = Company.builder()
                    .userId(user.getId())  // because Company stores `Long userId`
                    .name(user.getName())  // optional default
                    .verificationStatus(VerificationStatus.UNVERIFIED)
                    .build();
            companyRepository.save(company);
        }

        String token = jwtService.generateToken(saved);

        return AuthResponse.builder()
                .message("Registration successful")
                .token(token)
                .userId(saved.getId())
                .email(saved.getEmail())
                .role(request.getRole())
                .name(saved.getName())
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new RuntimeException("User not found");
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
}
