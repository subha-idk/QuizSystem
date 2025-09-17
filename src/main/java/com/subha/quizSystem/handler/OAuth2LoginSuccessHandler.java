package com.subha.quizSystem.handler;

import com.subha.quizSystem.model.User;
import com.subha.quizSystem.service.JWTService;
import com.subha.quizSystem.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService userService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        // --- FIX: Add a fallback for the username ---
        String username = oauth2User.getAttribute("email");
        if (username == null) {
            // If email is null (common with GitHub), use the 'login' attribute instead.
            username = oauth2User.getAttribute("login");
        }

        String name = oauth2User.getAttribute("name");
        // If the name is also null, use the username as the name.
        if (name == null) {
            name = username;
        }

        // --- Core Logic: Delegate to UserService ---
        User user = userService.processOAuthPostLogin(username, name);

        String role = user.getRole().name();
        String token = jwtService.generateToken(user.getUsername(), role);

        String targetUrl = frontendUrl + "/oauth2/redirect?token=" + token;
        response.sendRedirect(targetUrl);
    }
}