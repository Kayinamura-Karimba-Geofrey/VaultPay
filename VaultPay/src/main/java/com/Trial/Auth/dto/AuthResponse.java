package com.vaultpay.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}