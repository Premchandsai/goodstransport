package com.p2p.transport.service;

import com.p2p.transport.model.User;
import com.p2p.transport.model.enums.Role;
import com.p2p.transport.repository.UserRepository;
import com.p2p.transport.response.ApiResponse;
import com.p2p.transport.response.ErrorDetail;
import com.p2p.transport.service.NotificationService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @Transactional
    public ApiResponse<User> register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            ApiResponse<User> response = new ApiResponse<>(400, "Email already exists", null);
            response.setErrors(List.of(new ErrorDetail("EMAIL_EXISTS", "Email is already registered")));
            return response;
        }

        if (user.getRole() == null || !(user.getRole() == Role.SENDER || user.getRole() == Role.DRIVER || user.getRole() == Role.ADMIN)) {
            ApiResponse<User> response = new ApiResponse<>(400, "Invalid role", null);
            response.setErrors(List.of(new ErrorDetail("INVALID_ROLE", "Role must be SENDER, DRIVER, or ADMIN")));
            return response;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        notificationService.sendNotification(savedUser.getId(), "Welcome to P2P Transport!");
        return new ApiResponse<>(200, "User registered successfully", savedUser);
    }

    public ApiResponse<String> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            ApiResponse<String> response = new ApiResponse<>(401, "Invalid credentials", null);
            response.setErrors(List.of(new ErrorDetail("INVALID_CREDENTIALS", "Email or password is incorrect")));
            return response;
        }

        User user = userOpt.get();
        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        return new ApiResponse<>(200, "Login successful", token);
    }

    public ApiResponse<String> resetPassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            ApiResponse<String> response = new ApiResponse<>(404, "User not found", null);
            response.setErrors(List.of(new ErrorDetail("NOT_FOUND", "Email not registered")));
            return response;
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        notificationService.sendNotification(user.getId(), "Your password has been reset successfully");
        return new ApiResponse<>(200, "Password reset successful", null);
    }
}