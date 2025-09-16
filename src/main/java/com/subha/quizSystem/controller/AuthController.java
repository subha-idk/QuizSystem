package com.subha.quizSystem.controller;

import com.subha.quizSystem.config.SecurityConfig;
import com.subha.quizSystem.dao.UserRepository;
import com.subha.quizSystem.dto.AuthRequest;
import com.subha.quizSystem.dto.AuthResponse;
import com.subha.quizSystem.model.User;
import com.subha.quizSystem.service.JWTService;
import com.subha.quizSystem.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // This ensures the authentication was successful before generating token
        if (authentication.isAuthenticated()) {
            String role =  authentication.getAuthorities()
            .iterator()
            .next()
            .getAuthority()
            .replace("ROLE_", "");
            String token = jwtService.generateToken(request.getUsername(), role);
            return ResponseEntity.ok(new AuthResponse(token, request.getUsername(), role));
        } else {
            throw new RuntimeException("Invalid login credentials");
        }
    }



    @PostMapping("/register")
    public String register(@RequestBody User user) {
        userService.registerNewUser(user);
        return "User registered successfully!";
    }
}
