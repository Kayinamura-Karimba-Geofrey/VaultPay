package com.vaultpay.security.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        String endpoint = request.getRequestURI();

        // Apply strict limits only to sensitive endpoints
        if (endpoint.startsWith("/api/auth/login")
                || endpoint.startsWith("/api/transfer")) {

            boolean allowed = rateLimitService.allowRequest(ip + ":" + endpoint);

            if (!allowed) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many requests. Try again later.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}