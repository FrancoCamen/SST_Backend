package com.studytracker.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studytracker.backend.dto.AuthRequest;
import com.studytracker.backend.dto.AuthResponse;
import com.studytracker.backend.dto.RegisterRequest;
import com.studytracker.backend.service.AuthService;
import com.studytracker.backend.security.JwtService; 
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles; 

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test") 
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService; 

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should register a new user successfully")
    void shouldRegisterNewUserSuccessfully() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("password123");

        AuthResponse expectedResponse = new AuthResponse(
                "test-jwt-token",
                1L,
                "John Doe",
                "john@example.com"
        );

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                // CORRECCIÓN: Cambiado "$.id" a "$.userId"
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("Should return bad request when registering with existing email")
    void shouldReturnBadRequestWhenRegisteringWithExistingEmail() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("existing@example.com");
        registerRequest.setPassword("password123");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should login user successfully")
    void shouldLoginUserSuccessfully() throws Exception {
        // Given
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");

        AuthResponse expectedResponse = new AuthResponse(
                "test-jwt-token",
                1L,
                "John Doe",
                "john@example.com"
        );

        when(authService.login(any(AuthRequest.class)))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                // CORRECCIÓN: Cambiado "$.id" a "$.userId"
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("Should return unauthorized when login with invalid credentials")
    void shouldReturnUnauthorizedWhenLoginWithInvalidCredentials() throws Exception {
        // Given
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail("invalid@example.com");
        loginRequest.setPassword("wrongpassword");

        when(authService.login(any(AuthRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return test endpoint response")
    void shouldReturnTestEndpointResponse() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Auth endpoint is working"));
    }

    @Test
    @DisplayName("Should return validation error for invalid register request")
    void shouldReturnValidationErrorForInvalidRegisterRequest() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setName(""); // Invalid: empty name
        invalidRequest.setEmail("invalid-email"); // Invalid: not a valid email
        invalidRequest.setPassword("123"); // Invalid: too short

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error for invalid login request")
    void shouldReturnValidationErrorForInvalidLoginRequest() throws Exception {
        // Given
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setEmail(""); // Invalid: empty email
        invalidRequest.setPassword(""); // Invalid: empty password

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}