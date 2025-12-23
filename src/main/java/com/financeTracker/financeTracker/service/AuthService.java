package com.financeTracker.financeTracker.service;

import com.financeTracker.financeTracker.utils.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final int EXPIRY_MINUTES = 60;

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager,
                       JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public String login(String email, String password) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(email, password)
                );

        return jwtUtil.generateToken(authentication.getName(), EXPIRY_MINUTES);
    }
}
