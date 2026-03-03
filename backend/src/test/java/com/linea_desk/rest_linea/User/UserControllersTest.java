package com.linea_desk.rest_linea.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linea_desk.rest_linea.common.dto.LoginUserDto;
import com.linea_desk.rest_linea.common.dto.RegisterUserDto;
import com.linea_desk.rest_linea.common.service.AuthenticationService;
import com.linea_desk.rest_linea.common.service.JwtService;
import com.linea_desk.rest_linea.config.SecurityConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserControllers.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfiguration.class))
class UserControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserServices userServices;

    @MockitoBean
    private AuthenticationService authenticationService;

    private LoginUserDto loginRequest;
    private RegisterUserDto registerRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "testuser", "encodedPassword");

        loginRequest = new LoginUserDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterUserDto("test@example.com", "testuser", "password123");
    }

    // ========== POST /auth/login ==========

    @Test
    void login_Success() throws Exception {
        when(authenticationService.authenticate(any(LoginUserDto.class)))
                .thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(User.class)))
                .thenReturn("mock-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.jwtToken").value("mock-jwt-token"));
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        when(authenticationService.authenticate(any(LoginUserDto.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void login_JwtGenerationFails_ReturnsInternalServerError() throws Exception {
        when(authenticationService.authenticate(any(LoginUserDto.class)))
                .thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(User.class)))
                .thenReturn(null);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Internal Server Error"));
    }

    @Test
    void login_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(authenticationService.authenticate(any(LoginUserDto.class)))
                .thenThrow(new RuntimeException("Auth error"));

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ========== POST /auth/signup ==========

    @Test
    void signup_Success() throws Exception {
        UserResponseDto userResponse = new UserResponseDto("testuser", "test@example.com", "encodedPassword");
        when(authenticationService.signup(any(RegisterUserDto.class)))
                .thenReturn(Optional.of(userResponse));

        mockMvc.perform(post("/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    @Test
    void signup_DuplicateEmail_StillReturnsCreated() throws Exception {
        when(authenticationService.signup(any(RegisterUserDto.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void signup_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        when(authenticationService.signup(any(RegisterUserDto.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Internal Server Error"));
    }
}

