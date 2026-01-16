package com.example.VaultPay.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

@service
public class JwtService{
    private final string SECRET= "VaultPay_SECURE_SECRET_KEY";
    private final long EXPIRATION= 1000*60*15;
    public String generatedToken(string email){
        return Jwts.buider()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION))
                .signWith(SignatureAlgorithm.HS256,SECRET)
                .compact();
    }
    public String extractEmail(String token){
        return Jwts.parser().setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}

