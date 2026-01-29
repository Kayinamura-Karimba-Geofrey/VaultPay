package com.Trial.security;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@service
public class TokenBlacklistService{
    private final Set<String> blacklistedTokens = new HashSet<>();

    public void blacklistedToken(token){
        blacklistedTokens.add(token);
    }

}
public  boolean isTokenBlacklisted(String token){
    return blacklistedTokens.contains(token);

}