package com.VaultPay.security;
import io.jsonwebtoken.*;
import org.springframework.security.core.userdetails.userDetails;
import org.springframework.stereotype.Service;

@Service
public Class JwtService{
    private static final String SECRET_KEY= 'mySecretKey123456';
    private static final long EXPIRATION_TIME = 86400000;

    public string generateToken(UserDetails userDetails){
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(system.currentTimeMills()+ EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256,SECRET_KEY)
                .compact();

    }
    public string extractUsername(String token){
        return getClaims(token).getExpiration().before(new Date());

    }
    private  Claims getClaims(String token){
        return Jwt.parser();
        .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

    }
        }