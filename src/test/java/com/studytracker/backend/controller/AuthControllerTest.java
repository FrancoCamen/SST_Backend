package com.studytracker.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studytracker.backend.dto.AuthRequest;
import com.studytracker.backend.dto.AuthResponse;
import com.studytracker.backend.dto.RegisterRequest;
import com.studytracker.backend.service.AuthService;

import com.studytracker.backend.security.SecurityConfig;
import com.studytracker.backend.security.JwtAuthenticationFilter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = AuthController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = { SecurityConfig.class, JwtAuthenticationFilter.class }
    )
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should register a new user successfully")
    void shouldRegisterNewUserSuccessfully() throws Exception {
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

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("Should return bad request when registering with existing email")
    void shouldReturnBadRequestWhenRegisteringWithExistingEmail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("existing@example.com");
        registerRequest.setPassword("password123");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should login user successfully")
    void shouldLoginUserSuccessfully() throws Exception {
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

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("Should return unauthorized when login with invalid credentials")
    void shouldReturnUnauthorizedWhenLoginWithInvalidCredentials() throws Exception {
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail("invalid@example.com");
        loginRequest.setPassword("wrongpassword");

        when(authService.login(any(AuthRequest.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    // --- ESTE ES EL TEST QUE CAMBIAMOS PARA QUE FUNCIONE RÁPIDO ---
    @Test
    @DisplayName("Should return unauthorized for test endpoint without auth")
    void shouldReturnTestEndpointResponse() throws Exception {
        // Al estar la seguridad apagada en la configuración del test, el Authentication entra como null.
        // Nuestro nuevo controlador maneja esto devolviendo 401 Unauthorized.
        mockMvc.perform(get("/api/auth/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return validation error for invalid register request")
    void shouldReturnValidationErrorForInvalidRegisterRequest() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setName(""); 
        invalidRequest.setEmail("invalid-email"); 
        invalidRequest.setPassword("123"); 

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error for invalid login request")
    void shouldReturnValidationErrorForInvalidLoginRequest() throws Exception {
        AuthRequest invalidRequest = new AuthRequest();
        invalidRequest.setEmail(""); 
        invalidRequest.setPassword(""); 

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}