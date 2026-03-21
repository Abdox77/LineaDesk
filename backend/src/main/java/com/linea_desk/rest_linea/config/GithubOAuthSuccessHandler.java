package com.linea_desk.rest_linea.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.linea_desk.rest_linea.User.User;
import com.linea_desk.rest_linea.User.UserRepository;
import com.linea_desk.rest_linea.common.service.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class GithubOAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LogManager.getLogger(GithubOAuthSuccessHandler.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${frontend.base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    public GithubOAuthSuccessHandler(
            UserRepository userRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();

        String email = (String) attributes.get("email");
        String login = (String) attributes.get("login");
        String name  = (String) attributes.getOrDefault("name", login);

        if (email == null || email.isBlank()) {
            email = login + "@users.noreply.github.com";
        }

        final String finalEmail = email;
        final String displayName = (name != null && !name.isBlank()) ? name : login;

        User user = userRepository.findByEmail(finalEmail).orElseGet(() -> {
            log.info("Creating new user from GitHub OAuth: {}", finalEmail);
            User newUser = new User()
                    .setEmail(finalEmail)
                    .setUsername(displayName)
                    .setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            return userRepository.save(newUser);
        });

        String jwt = jwtService.generateToken(user);
        String redirectUrl = frontendBaseUrl + "/oauth/callback?token=" + jwt
                + "&id=" + user.getUserId()
                + "&email=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8)
                + "&username=" + URLEncoder.encode(user.getDisplayName(), StandardCharsets.UTF_8);
        log.info("GitHub OAuth successful for {}. Redirecting to frontend.", finalEmail);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
