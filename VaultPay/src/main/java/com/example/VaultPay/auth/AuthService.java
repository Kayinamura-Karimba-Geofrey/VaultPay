package com.vaultpay.auth;

import com.vaultpay.auth.dto.*;
import com.vaultpay.security.JwtService;
import com.vaultpay.user.*;
import com.vaultpay.wallet.*;
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

    public AuthResponse register(RegisterRequest request) {

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        Wallet wallet = Wallet.builder()
                .owner(user)
                .balance(BigDecimal.ZERO)
                .build();

        walletRepository.save(wallet);

        return new AuthResponse(jwtService.generateToken(user.getEmail()));
    }
}
