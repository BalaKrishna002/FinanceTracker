package com.financeTracker.financeTracker.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financeTracker.financeTracker.DTO.LoginRequestDTO;
import com.financeTracker.financeTracker.utils.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Deprecated(since = "We are using /auth/login in UserAuthController")
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    private static final int expiryMinutes = 15;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(!request.getServletPath().equals("/auth/login")){
            filterChain.doFilter(request, response);
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        LoginRequestDTO loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDTO.class);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authResult = authenticationManager.authenticate(authToken);
        System.out.println(authResult.getName());
        if(authResult.isAuthenticated()){
            String token = jwtUtil.generateToken(loginRequest.getEmail(), expiryMinutes);
            response.setHeader("Authorization","Bearer "+token);
        }
    }
}
