package com.financeTracker.financeTracker.controller;

import com.financeTracker.financeTracker.DTO.JwtTokenResponseDTO;
import com.financeTracker.financeTracker.DTO.LoginRequestDTO;
import com.financeTracker.financeTracker.DTO.UserDetailsResponseDTO;
import com.financeTracker.financeTracker.DTO.UserRegisterRequestDTO;
import com.financeTracker.financeTracker.service.AuthService;
import com.financeTracker.financeTracker.service.UserService;
import com.financeTracker.financeTracker.utils.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserAuthController {

    private static final int EXPIRY_MINUTES = 60;

    private final UserService userService;
    private final AuthService authService;

    public UserAuthController(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTUtil jwtUtil, AuthenticationManager authenticationManager1, JWTUtil jwtUtil1, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterRequestDTO userDetails) {
        userService.register(userDetails);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<JwtTokenResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(new JwtTokenResponseDTO(token));
    }

    @GetMapping("/user/{id}")
    public UserDetailsResponseDTO getUserDetails(@PathVariable Long id){
        return userService.getUserDetails(id);
    }
}

