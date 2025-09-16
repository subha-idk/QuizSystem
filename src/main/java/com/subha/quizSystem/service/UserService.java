package com.subha.quizSystem.service;

import com.subha.quizSystem.dao.UserRepository;
import com.subha.quizSystem.model.User;
import com.subha.quizSystem.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID; // Import UUID for generating random passwords

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerNewUser(User user) {
        // Set the default role for all new form-based registrations
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * ADD THIS NEW METHOD
     * This method handles the logic for finding an existing user by their email
     * or creating a new one if they don't exist. This is specifically for OAuth2 logins.
     */
    public User processOAuthPostLogin(String email, String name) {
        return userRepository.findByUsername(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    
                    newUser.setUsername(email); 
                    newUser.setRole(Role.USER);
                    newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    
                    return userRepository.save(newUser);
                });
    }
}