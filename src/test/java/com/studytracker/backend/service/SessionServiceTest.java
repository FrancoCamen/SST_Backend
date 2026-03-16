package com.studytracker.backend.service;

import com.studytracker.backend.dto.SessionRequest;
import com.studytracker.backend.dto.SessionResponse;
import com.studytracker.backend.model.Folder;
import com.studytracker.backend.model.Session;
import com.studytracker.backend.model.Tag;
import com.studytracker.backend.model.AppUser;
import com.studytracker.backend.repository.FolderRepository;
import com.studytracker.backend.repository.SessionRepository;
import com.studytracker.backend.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    @DisplayName("Should create session successfully")
    void shouldCreateSessionSuccessfully() {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);

        Folder folder = new Folder();
        folder.setId(1L);
        folder.setName("Mathematics");
        folder.setUser(user);

        SessionRequest request = new SessionRequest();
        request.setTitle("Math Study Session");
        request.setDescription("Studying calculus");
        request.setStartTime(LocalDateTime.now().minusHours(2));
        request.setEndTime(LocalDateTime.now().minusHours(1));
        request.setFolderId(1L);
        request.setTags(Arrays.asList("math", "calculus"));

        // Definición de etiquetas para evitar el error "cannot find symbol"
        Tag mathTag = new Tag("math");
        mathTag.setId(1L);
        Tag calculusTag = new Tag("calculus");
        calculusTag.setId(2L);

        Session savedSession = new Session();
        savedSession.setId(1L);
        savedSession.setTitle("Math Study Session");
        savedSession.setDescription("Studying calculus");
        savedSession.setStartTime(request.getStartTime());
        savedSession.setEndTime(request.getEndTime());
        savedSession.setDurationMinutes(60);
        savedSession.setUser(user);
        savedSession.setFolder(folder);
        savedSession.setCreatedAt(LocalDateTime.now());
        savedSession.setTags(java.util.List.of(mathTag, calculusTag)); // Asignamos las etiquetas al objeto simulado

        when(folderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(folder));
        when(tagRepository.findByName("math")).thenReturn(Optional.of(mathTag));
        when(tagRepository.findByName("calculus")).thenReturn(Optional.of(calculusTag));
        when(sessionRepository.save(any(Session.class))).thenReturn(savedSession);

        // When
        SessionResponse result = sessionService.createSession(request, user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTags()).containsExactlyInAnyOrder("math", "calculus");
        
        verify(folderRepository).findByIdAndUser(1L, user);
        verify(tagRepository, atLeastOnce()).findByName(any(String.class));
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    @DisplayName("Should get session by id successfully")
    void shouldGetSessionByIdSuccessfully() {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);

        Folder folder = new Folder();
        folder.setId(1L);
        folder.setName("Mathematics");

        Session session = new Session();
        session.setId(1L);
        session.setTitle("Math Study Session");
        session.setDescription("Studying calculus");
        session.setStartTime(LocalDateTime.now().minusHours(2));
        session.setEndTime(LocalDateTime.now().minusHours(1));
        session.setDurationMinutes(60);
        session.setUser(user);
        session.setFolder(folder);
        session.setCreatedAt(LocalDateTime.now()); // Aseguramos que no sea null para la aserción

        when(sessionRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(session));

        // When
        SessionResponse result = sessionService.getSessionById(1L, user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCreatedAt()).isNotNull();
        
        verify(sessionRepository).findByIdAndUser(1L, user);
    }

    @Test
    @DisplayName("Should create session with new tags successfully")
    void shouldCreateSessionWithNewTagsSuccessfully() {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);

        SessionRequest request = new SessionRequest();
        request.setTitle("Physics Study Session");
        request.setStartTime(LocalDateTime.now().minusHours(1));
        request.setEndTime(LocalDateTime.now());
        request.setTags(Arrays.asList("physics", "quantum"));

        // Definición de etiquetas
        Tag physicsTag = new Tag("physics");
        physicsTag.setId(1L);
        Tag quantumTag = new Tag("quantum");
        quantumTag.setId(2L);

        Session savedSession = new Session();
        savedSession.setId(1L);
        savedSession.setTitle("Physics Study Session");
        savedSession.setDurationMinutes(60);
        savedSession.setUser(user);
        savedSession.setTags(java.util.List.of(physicsTag, quantumTag));

        when(folderRepository.findByIdAndUser(any(Long.class), any(AppUser.class))).thenReturn(Optional.empty());
        when(tagRepository.findByName("physics")).thenReturn(Optional.of(physicsTag));
when(tagRepository.findByName("quantum")).thenReturn(Optional.empty()); // Simulamos que esta es la nueva
        when(tagRepository.save(any(Tag.class))).thenReturn(quantumTag);
        when(sessionRepository.save(any(Session.class))).thenReturn(savedSession);

        // When
        SessionResponse result = sessionService.createSession(request, user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTags()).contains("physics", "quantum");
        
        verify(tagRepository, atLeastOnce()).findByName(any(String.class));
        verify(tagRepository).save(any(Tag.class));
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    @DisplayName("Should get sessions by folder successfully")
    void shouldGetSessionsByFolderSuccessfully() {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);

        Folder folder = new Folder();
        folder.setId(1L);
        folder.setName("Mathematics");
        folder.setUser(user);

        Session session1 = new Session();
        session1.setId(1L);
        session1.setTitle("Session 1");
        session1.setDurationMinutes(60);
        session1.setUser(user);
        session1.setFolder(folder);

        Session session2 = new Session();
        session2.setId(2L);
        session2.setTitle("Session 2");
        session2.setDurationMinutes(90);
        session2.setUser(user);
        session2.setFolder(folder);

        List<Session> sessions = Arrays.asList(session1, session2);

        when(folderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(folder));
        when(sessionRepository.findByUserAndFolder(user, folder)).thenReturn(sessions);

        // When
        List<SessionResponse> result = sessionService.getUserSessionsByFolder(1L, user);

        // Then
        assertThat(result).hasSize(2);
        verify(sessionRepository).findByUserAndFolder(user, folder);
    }

    @Test
    @DisplayName("Should throw exception when folder not found")
    void shouldThrowExceptionWhenFolderNotFound() {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);

        SessionRequest request = new SessionRequest();
        request.setFolderId(1L);

        when(folderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sessionService.createSession(request, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Folder not found");
    }

    @Test
    @DisplayName("Should delete session successfully")
    void shouldDeleteSessionSuccessfully() {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);

        Session session = new Session();
        session.setId(1L);
        session.setUser(user);

        when(sessionRepository.existsById(1L)).thenReturn(true);
        when(sessionRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(session));
        doNothing().when(sessionRepository).delete(session);

        // When
        sessionService.deleteSession(1L, user);

        // Then
        verify(sessionRepository).delete(session);
    }

    @Test
    @DisplayName("Should throw exception when deleting session not found")
    void shouldThrowExceptionWhenDeletingSessionNotFound() {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);

        when(sessionRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> sessionService.deleteSession(1L, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Session not found");
    }

    @Test
    @DisplayName("Should get user sessions successfully")
    void shouldGetUserSessionsSuccessfully() {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);

        Folder f1 = new Folder();
        f1.setName("Mathematics");
        Session s1 = new Session();
        s1.setId(1L);
        s1.setTitle("Math Session");
        s1.setFolder(f1);
        s1.setUser(user);

        List<Session> sessions = List.of(s1);

        when(sessionRepository.findByUser(user)).thenReturn(sessions);

        // When
        List<SessionResponse> result = sessionService.getUserSessions(user);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Math Session");
    }

    @Test
    @DisplayName("Should throw exception when getting session by id not found")
    void shouldThrowExceptionWhenGettingSessionByIdNotFound() {
        // Given
        AppUser user = new AppUser();
        user.setId(1L);

        when(sessionRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sessionService.getSessionById(1L, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Session not found");
    }
}
