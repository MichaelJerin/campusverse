package com.campusverse.service;

import com.campusverse.config.JwtService;
import com.campusverse.dto.AuthResponse;
import com.campusverse.dto.LoginRequest;
import com.campusverse.dto.RegisterRequest;
import com.campusverse.model.Role;
import com.campusverse.model.User;
import com.campusverse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${app.auth.allowed-email-domain}")
    private String allowedEmailDomain;

    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (!normalizedEmail.endsWith("@" + allowedEmailDomain.toLowerCase())) {
            throw new IllegalArgumentException("Registration requires a @" + allowedEmailDomain + " email address");
        }

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .branch(request.getBranch())
                .degreeLevel(request.getDegreeLevel())
                .batchStartYear(request.getBatchStartYear())
                .batchEndYear(request.getBatchEndYear())
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole().name());
    }
}