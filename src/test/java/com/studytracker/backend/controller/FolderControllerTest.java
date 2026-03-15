package com.studytracker.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studytracker.backend.dto.FolderRequest;
import com.studytracker.backend.dto.FolderResponse;
import com.studytracker.backend.model.User;
import com.studytracker.backend.security.JwtService;
import com.studytracker.backend.service.FolderService;
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

@WebMvcTest(controllers = FolderController.class)
@ActiveProfiles("test")
@DisplayName("Folder Controller Tests")
class FolderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FolderService folderService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    // Configuración manual del contexto de seguridad para usar nuestra propia clase User
    @BeforeEach
    void setUpSecurityContext() {
        User myCustomUser = new User();
        myCustomUser.setId(1L);
        myCustomUser.setEmail("test@example.com");
        // Borramos el setPassword porque no lo necesitamos ni existe

        // En lugar de pedirle las authorities al usuario, le pasamos una lista vacía
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(myCustomUser, null, java.util.List.of());
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("Should create folder successfully")
    void shouldCreateFolderSuccessfully() throws Exception {
        // Given
        FolderRequest request = new FolderRequest();
        request.setName("Mathematics");
        request.setDescription("Study folder for mathematics subjects");

        FolderResponse expectedResponse = new FolderResponse(
                1L,
                "Mathematics",
                "Study folder for mathematics subjects",
                0L,
                0L,
                null,
                LocalDateTime.now()
        );

        when(folderService.createFolder(any(FolderRequest.class), any(User.class)))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/folders")
                        .with(csrf()) // Evitamos el 403 Forbidden
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Mathematics"))
                .andExpect(jsonPath("$.description").value("Study folder for mathematics subjects"))
                .andExpect(jsonPath("$.totalHours").value(0))
                .andExpect(jsonPath("$.sessionCount").value(0))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Should get user folders successfully")
    void shouldGetUserFoldersSuccessfully() throws Exception {
        // Given
        List<FolderResponse> expectedFolders = Arrays.asList(
                new FolderResponse(1L, "Mathematics", "Math studies", 10L, 5L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(30)),
                new FolderResponse(2L, "Physics", "Physics studies", 8L, 3L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(25))
        );

        when(folderService.getUserFolders(any(User.class)))
                .thenReturn(expectedFolders);

        // When & Then (Los GET no necesitan CSRF)
        mockMvc.perform(get("/api/folders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Mathematics"))
                .andExpect(jsonPath("$[0].totalHours").value(10))
                .andExpect(jsonPath("$[0].sessionCount").value(5))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Physics"))
                .andExpect(jsonPath("$[1].totalHours").value(8))
                .andExpect(jsonPath("$[1].sessionCount").value(3));
    }

    @Test
    @DisplayName("Should get folder by id successfully")
    void shouldGetFolderByIdSuccessfully() throws Exception {
        // Given
        FolderResponse expectedFolder = new FolderResponse(
                1L,
                "Mathematics",
                "Math studies",
                10L,
                5L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(30)
        );

        when(folderService.getFolderById(eq(1L), any(User.class)))
                .thenReturn(expectedFolder);

        // When & Then
        mockMvc.perform(get("/api/folders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Mathematics"))
                .andExpect(jsonPath("$.description").value("Math studies"))
                .andExpect(jsonPath("$.totalHours").value(10))
                .andExpect(jsonPath("$.sessionCount").value(5))
                .andExpect(jsonPath("$.lastSessionDate").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("Should delete folder successfully")
    void shouldDeleteFolderSuccessfully() throws Exception {
        // Given
        doNothing().when(folderService).deleteFolder(eq(1L), any(User.class));

        // When & Then
        mockMvc.perform(delete("/api/folders/1")
                        .with(csrf())) // Evitamos el 403 Forbidden
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return validation error when creating folder with empty name")
    void shouldReturnValidationErrorWhenCreatingFolderWithEmptyName() throws Exception {
        // Given
        FolderRequest invalidRequest = new FolderRequest();
        invalidRequest.setName(""); // Invalid: empty name
        invalidRequest.setDescription("Some description");

        // When & Then
        mockMvc.perform(post("/api/folders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when creating folder with name too long")
    void shouldReturnValidationErrorWhenCreatingFolderWithTooLongName() throws Exception {
        // Given
        FolderRequest invalidRequest = new FolderRequest();
        invalidRequest.setName("a".repeat(101)); // Invalid: name exceeds 100 characters
        invalidRequest.setDescription("Some description");

        // When & Then
        mockMvc.perform(post("/api/folders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when creating folder with description too long")
    void shouldReturnValidationErrorWhenCreatingFolderWithTooLongDescription() throws Exception {
        // Given
        FolderRequest invalidRequest = new FolderRequest();
        invalidRequest.setName("Valid Name");
        invalidRequest.setDescription("a".repeat(501)); // Invalid: description exceeds 500 characters

        // When & Then
        mockMvc.perform(post("/api/folders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
