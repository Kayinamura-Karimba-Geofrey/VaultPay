package com.vaultpay.auth;

import com.vaultpay.audit.AuditAction;
import com.vaultpay.audit.AuditService;
import com.vaultpay.auth.dto.AuthResponse;
import com.vaultpay.auth.dto.LoginRequest;
import com.vaultpay.auth.dto.RegisterRequest;
import com.vaultpay.security.JwtService;
import com.vaultpay.security.TokenBlacklist;
import com.vaultpay.security.TokenBlacklistRepository;
import com.vaultpay.user.RoleType;
import com.vaultpay.user.User;
import com.vaultpay.user.UserRepository;
import com.vaultpay.wallet.Wallet;
import com.vaultpay.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistRepository blacklistRepository;
    private final AuditService auditService;

    /**
     * USER REGISTRATION
     * - Hash password
     * - Create wallet
     * - Assign USER role
     * - Issue tokens
     */
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(RoleType.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        // Auto-create wallet with zero balance
        Wallet wallet = Wallet.builder()
                .owner(user)
                .balance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .build();

        walletRepository.save(wallet);

        auditService.log(
                AuditAction.USER_REGISTERED,
                user,
                "New user registered"
        );

        return new AuthResponse(
                jwtService.generateAccessToken(user.getEmail()),
                jwtService.generateRefreshToken(user.getEmail())
        );
    }

    /**
     * LOGIN
     */
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        auditService.log(
                AuditAction.USER_LOGGED_IN,
                user,
                "User logged in"
        );

        return new AuthResponse(
                jwtService.generateAccessToken(user.getEmail()),
                jwtService.generateRefreshToken(user.getEmail())
        );
    }

    /**
     * LOGOUT (blacklist token)
     */
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
}