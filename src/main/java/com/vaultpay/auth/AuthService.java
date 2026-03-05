package com.vaultpay.auth;

import com.vaultpay.auth.dto.AuthResponse;
import com.vaultpay.auth.dto.LoginRequest;
import com.vaultpay.audit.AuditAction;
import com.vaultpay.audit.AuditService;
import com.vaultpay.security.JwtService;
import com.vaultpay.security.TokenBlacklist;
import com.vaultpay.security.TokenBlacklistRepository;
import com.vaultpay.user.User;
import com.vaultpay.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistRepository blacklistRepository;
    private final AuditService auditService;


    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }


        auditService.log(
                AuditAction.USER_LOGGED_IN,
                user,
                "User logged in successfully"
        );

        return new AuthResponse(
                jwtService.generateAccessToken(user.getEmail()),
                jwtService.generateRefreshToken(user.getEmail())
        );
    }


    public void logout(String token) {

        String email = jwtService.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        blacklistRepository.save(
                TokenBlacklist.builder()
                        .token(token)
                        .invalidatedAt(LocalDateTime.now())
                        .build()
        );


        auditService.log(
                AuditAction.USER_LOGGED_OUT,
                user,
                "User logged out"
        );
    }

    public AuthResponse refresh(com.vaultpay.auth.dto.RefreshRequest request) {
        String email = jwtService.extractEmail(request.getRefreshToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(
                jwtService.generateAccessToken(user.getEmail()),
                jwtService.generateRefreshToken(user.getEmail())
        );
    }
}
