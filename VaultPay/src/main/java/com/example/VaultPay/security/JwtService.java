package com.example.VaultPay.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final String SECRET = "VAULTPAY_SUPER_SECURE_SECRET_KEY";
    private final long ACCESS_EXP = 1000 * 60 * 15;       // 15 min
    private final long REFRESH_EXP = 1000 * 60 * 60 * 24 * 7; // 7 days

    public String generateAccessToken(String email) {
        return buildToken(email, ACCESS_EXP);
    }

    public String generateRefreshToken(String email) {
        return buildToken(email, REFRESH_EXP);
    }

    private String buildToken(String email, long expiration) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
