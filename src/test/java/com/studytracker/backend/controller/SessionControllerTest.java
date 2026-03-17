package com.studytracker.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studytracker.backend.dto.SessionRequest;
import com.studytracker.backend.dto.SessionResponse;
import com.studytracker.backend.model.AppUser;
import com.studytracker.backend.security.JwtService;
import com.studytracker.backend.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.studytracker.backend.security.CustomUserDetails;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SessionController.class)
@ActiveProfiles("test")
@DisplayName("Session Controller Tests")
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpSecurityContext() {
        AppUser myCustomUser = new AppUser();
        myCustomUser.setId(1L);
        myCustomUser.setEmail("test@example.com");

        // Envolvemos el AppUser en CustomUserDetails
        CustomUserDetails userDetails = new CustomUserDetails(myCustomUser);

        // El principal ahora es el objeto userDetails
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("Should create session successfully")
    void shouldCreateSessionSuccessfully() throws Exception {
        // Given
        SessionRequest request = new SessionRequest();
        request.setTitle("Math Study Session");
        request.setDescription("Studying calculus and algebra");
        request.setStartTime(LocalDateTime.now().minusHours(2));
        request.setEndTime(LocalDateTime.now().minusHours(1));
        request.setFolderId(1L);
        request.setTags(Arrays.asList("math", "calculus", "algebra"));

        SessionResponse expectedResponse = new SessionResponse(
                1L,
                "Math Study Session",
                "Studying calculus and algebra",
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                60,
                1L,
                "Mathematics",
                Arrays.asList("math", "calculus", "algebra"),
                LocalDateTime.now()
        );

        when(sessionService.createSession(any(SessionRequest.class), any(AppUser.class)))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/sessions")
                        .with(csrf()) // Evitamos el 403 Forbidden
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Math Study Session"))
                .andExpect(jsonPath("$.description").value("Studying calculus and algebra"))
                .andExpect(jsonPath("$.durationMinutes").value(60))
                .andExpect(jsonPath("$.folderId").value(1))
                .andExpect(jsonPath("$.folderName").value("Mathematics"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags[0]").value("math"))
                .andExpect(jsonPath("$.tags[1]").value("calculus"))
                .andExpect(jsonPath("$.tags[2]").value("algebra"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Should get sessions by folder id successfully")
    void shouldGetSessionsByFolderIdSuccessfully() throws Exception {
        // Given
        List<SessionResponse> expectedSessions = Arrays.asList(
                new SessionResponse(1L, "Session 1", "Description 1", 
                    LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2).plusHours(1), 60, 
                    1L, "Mathematics", Arrays.asList("math"), LocalDateTime.now().minusDays(2)),
                new SessionResponse(2L, "Session 2", "Description 2", 
                    LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1).plusHours(2), 120, 
                    1L, "Mathematics", Arrays.asList("algebra"), LocalDateTime.now().minusDays(1))
        );

        when(sessionService.getUserSessionsByFolder(eq(1L), any(AppUser.class)))
                .thenReturn(expectedSessions);

        // When & Then (Los GET no necesitan CSRF)
        mockMvc.perform(get("/api/sessions/folder/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Session 1"))
                .andExpect(jsonPath("$[0].folderId").value(1))
                .andExpect(jsonPath("$[0].folderName").value("Mathematics"))
                .andExpect(jsonPath("$[0].durationMinutes").value(60))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Session 2"))
                .andExpect(jsonPath("$[1].durationMinutes").value(120));
    }

    @Test
    @DisplayName("Should delete session successfully")
    void shouldDeleteSessionSuccessfully() throws Exception {
        // Given
        doNothing().when(sessionService).deleteSession(eq(1L), any(AppUser.class));

        // When & Then
        mockMvc.perform(delete("/api/sessions/1")
                        .with(csrf())) // Evitamos el 403 Forbidden
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return validation error when creating session without title")
    void shouldReturnValidationErrorWhenCreatingSessionWithoutTitle() throws Exception {
        // Given
        SessionRequest invalidRequest = new SessionRequest();
        invalidRequest.setTitle(""); // Invalid: empty title
        invalidRequest.setDescription("Some description");
        invalidRequest.setStartTime(LocalDateTime.now().minusHours(2));
        invalidRequest.setEndTime(LocalDateTime.now().minusHours(1));
        invalidRequest.setFolderId(1L);

        // When & Then
        mockMvc.perform(post("/api/sessions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when creating session without start time")
    void shouldReturnValidationErrorWhenCreatingSessionWithoutStartTime() throws Exception {
        // Given
        SessionRequest invalidRequest = new SessionRequest();
        invalidRequest.setTitle("Valid Title");
        invalidRequest.setDescription("Some description");
        invalidRequest.setStartTime(null); // Invalid: null start time
        invalidRequest.setEndTime(LocalDateTime.now().minusHours(1));
        invalidRequest.setFolderId(1L);

        // When & Then
        mockMvc.perform(post("/api/sessions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when creating session without end time")
    void shouldReturnValidationErrorWhenCreatingSessionWithoutEndTime() throws Exception {
        // Given
        SessionRequest invalidRequest = new SessionRequest();
        invalidRequest.setTitle("Valid Title");
        invalidRequest.setDescription("Some description");
        invalidRequest.setStartTime(LocalDateTime.now().minusHours(2));
        invalidRequest.setEndTime(null); // Invalid: null end time
        invalidRequest.setFolderId(1L);

        // When & Then
        mockMvc.perform(post("/api/sessions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get session by id successfully")
    void shouldGetSessionByIdSuccessfully() throws Exception {
        // Given
        SessionResponse expectedSession = new SessionResponse(
                1L,
                "Math Study Session",
                "Studying calculus",
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                60,
                1L,
                "Mathematics",
                Arrays.asList("math", "calculus"),
                LocalDateTime.now()
        );

        when(sessionService.getSessionById(eq(1L), any(AppUser.class)))
                .thenReturn(expectedSession);

        // When & Then (Los GET no necesitan CSRF)
        mockMvc.perform(get("/api/sessions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Math Study Session"))
                .andExpect(jsonPath("$.description").value("Studying calculus"))
                .andExpect(jsonPath("$.durationMinutes").value(60))
                .andExpect(jsonPath("$.folderId").value(1))
                .andExpect(jsonPath("$.folderName").value("Mathematics"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags[0]").value("math"))
                .andExpect(jsonPath("$.tags[1]").value("calculus"));
    }
}
