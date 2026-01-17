package com.vaultpay.auth;

import com.vaultpay.auth.dto.*;
import com.vaultpay.security.*;
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

    public AuthResponse login(LoginRequest request) {

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return new AuthResponse(
                jwtService.generateAccessToken(user.getEmail()),
                jwtService.generateRefreshToken(user.getEmail())
        );
    }

    public AuthResponse refresh(RefreshRequest request) {

        String email = jwtService.extractEmail(request.getRefreshToken());

        return new AuthResponse(
                jwtService.generateAccessToken(email),
                request.getRefreshToken()
        );
    }

    public void logout(String token) {
        blacklistRepository.save(
                TokenBlacklist.builder()
                        .token(token)
                        .invalidatedAt(LocalDateTime.now())
                        .build()
        );
    }
}
