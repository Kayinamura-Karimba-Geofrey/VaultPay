package com.VaultPay.security;

import jakarta.servlets.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import  lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.securityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@component
@RequiredArgsConstructor
public Class JwtAuthenticationFilter extends OncePerRequestFilter{
    public final JwtService jwtService;
    public final  CustomUserDetailsService customUserDetailsService;
    private final TokenBlacklistService tokenBlacklistService;


    @Override
     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response , FilterChain  filterChain) throws java.io.IOException  jakarta.servlet.ServletException{
        String authHeader= request.getHeader("Authorisation");
        if (authHeader=null || !authHeader. startWith("Bearer")){
            FilterChain.doFilter(Request, response )
        return;

    }
        String jwt = authHeader.substring(7);
        if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }
        String username= jwtService.extractUsername(jwt);

        if(username!=null&& SecurityContextHolder.getContext().getAuthentication()=null){
            var userDetails= userDetailsService.loUserByUsername(username);
            if(jwtService.isTokenvalid(jwt,userDetails)){
                UsernamePasswordAuthenticationToken authToken= new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request,response);
}