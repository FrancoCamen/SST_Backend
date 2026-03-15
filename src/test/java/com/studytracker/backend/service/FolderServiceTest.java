package com.studytracker.backend.service;

import com.studytracker.backend.dto.FolderRequest;
import com.studytracker.backend.dto.FolderResponse;
import com.studytracker.backend.model.Folder;
import com.studytracker.backend.model.User;
import com.studytracker.backend.repository.FolderRepository;
import com.studytracker.backend.repository.SessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Folder Service Tests")
class FolderServiceTest {

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private FolderService folderService;

    private User testUser;

    @Test
    @DisplayName("Should create folder successfully")
    void shouldCreateFolderSuccessfully() {
        // Given
        FolderRequest request = new FolderRequest();
        request.setName("Mathematics");
        request.setDescription("Study folder for mathematics subjects");

        User user = new User();
        user.setId(1L);

        Folder savedFolder = new Folder();
        savedFolder.setId(1L);
        savedFolder.setName("Mathematics");
        savedFolder.setDescription("Study folder for mathematics subjects");
        savedFolder.setUser(user);
        savedFolder.setCreatedAt(LocalDateTime.now());

        when(folderRepository.save(any(Folder.class))).thenReturn(savedFolder);
        when(sessionRepository.totalMinutesByUserSince(eq(user), any(LocalDateTime.class))).thenReturn(0L);
        when(sessionRepository.countSessionsByUserSince(eq(user), any(LocalDateTime.class))).thenReturn(0L);
        when(sessionRepository.findByUser(user)).thenReturn(Arrays.asList());

        // When
        FolderResponse result = folderService.createFolder(request, user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Mathematics");
        assertThat(result.getDescription()).isEqualTo("Study folder for mathematics subjects");
        assertThat(result.getTotalHours()).isEqualTo(0L);
        assertThat(result.getSessionCount()).isEqualTo(0L);
        assertThat(result.getCreatedAt()).isNotNull();
        
        verify(folderRepository).save(any(Folder.class));
        verify(sessionRepository, atLeastOnce()).totalMinutesByUserSince(eq(user), any(LocalDateTime.class));
        verify(sessionRepository, atLeastOnce()).countSessionsByUserSince(eq(user), any(LocalDateTime.class));
        verify(sessionRepository, atLeastOnce()).findByUser(user);
    }

    @Test
    @DisplayName("Should get user folders successfully")
    void shouldGetUserFoldersSuccessfully() {
        // Given
        User user = new User();
        user.setId(1L);

        Folder folder1 = new Folder();
        folder1.setId(1L);
        folder1.setName("Mathematics");
        folder1.setUser(user);
        folder1.setCreatedAt(LocalDateTime.now().minusDays(30));

        Folder folder2 = new Folder();
        folder2.setId(2L);
        folder2.setName("Physics");
        folder2.setUser(user);
        folder2.setCreatedAt(LocalDateTime.now().minusDays(25));

        List<Folder> userFolders = Arrays.asList(folder1, folder2);

        when(folderRepository.findByUser(user)).thenReturn(userFolders);
        when(sessionRepository.totalMinutesByUserSince(eq(user), any(LocalDateTime.class))).thenReturn(120L);
        when(sessionRepository.countSessionsByUserSince(eq(user), any(LocalDateTime.class))).thenReturn(8L);
        when(sessionRepository.findByUser(user)).thenReturn(Arrays.asList());

        // When
        List<FolderResponse> result = folderService.getUserFolders(user);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Mathematics");
        assertThat(result.get(0).getTotalHours()).isEqualTo(2L); // 120 minutes / 60 = 2 hours
        assertThat(result.get(0).getSessionCount()).isEqualTo(8L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getName()).isEqualTo("Physics");
        assertThat(result.get(1).getTotalHours()).isEqualTo(2L);
        assertThat(result.get(1).getSessionCount()).isEqualTo(8L);
        
        verify(folderRepository).findByUser(user);
        verify(sessionRepository, atLeastOnce()).totalMinutesByUserSince(eq(user), any(LocalDateTime.class));
        verify(sessionRepository, atLeastOnce()).countSessionsByUserSince(eq(user), any(LocalDateTime.class));
        verify(sessionRepository, atLeastOnce()).findByUser(user);
    }

    @Test
    @DisplayName("Should get folder by id successfully")
    void shouldGetFolderByIdSuccessfully() {
        // Given
        User user = new User();
        user.setId(1L);

        Folder folder = new Folder();
        folder.setId(1L);
        folder.setName("Mathematics");
        folder.setUser(user);

        when(folderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(folder));
        when(sessionRepository.totalMinutesByUserSince(eq(user), any(LocalDateTime.class))).thenReturn(90L);
        when(sessionRepository.countSessionsByUserSince(eq(user), any(LocalDateTime.class))).thenReturn(5L);
        when(sessionRepository.findByUser(user)).thenReturn(Arrays.asList());

        // When
        FolderResponse result = folderService.getFolderById(1L, user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Mathematics");
        assertThat(result.getTotalHours()).isEqualTo(1L); // 90 minutes / 60 = 1.5 hours -> 1L (truncated)
        assertThat(result.getSessionCount()).isEqualTo(5L);
        
        verify(folderRepository).findByIdAndUser(1L, user);
        verify(sessionRepository, atLeastOnce()).totalMinutesByUserSince(eq(user), any(LocalDateTime.class));
        verify(sessionRepository, atLeastOnce()).countSessionsByUserSince(eq(user), any(LocalDateTime.class));
        verify(sessionRepository, atLeastOnce()).findByUser(user);
    }

    @Test
    @DisplayName("Should throw exception when folder not found by id")
    void shouldThrowExceptionWhenFolderNotFoundById() {
        // Given
        User user = new User();
        user.setId(1L);

        when(folderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> folderService.getFolderById(1L, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Folder not found");
        
        verify(folderRepository).findByIdAndUser(1L, user);
    }

    @Test
    @DisplayName("Should update folder successfully")
    void shouldUpdateFolderSuccessfully() {
        // Given
        User user = new User();
        user.setId(1L);

        FolderRequest request = new FolderRequest();
        request.setName("Updated Mathematics");
        request.setDescription("Updated description");

        Folder existingFolder = new Folder();
        existingFolder.setId(1L);
        existingFolder.setName("Mathematics");
        existingFolder.setUser(user);

        Folder updatedFolder = new Folder();
        updatedFolder.setId(1L);
        updatedFolder.setName("Updated Mathematics");
        updatedFolder.setDescription("Updated description");
        updatedFolder.setUser(user);

        when(folderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existingFolder));
        when(folderRepository.save(any(Folder.class))).thenReturn(updatedFolder);
        when(sessionRepository.totalMinutesByUserSince(eq(user), any(LocalDateTime.class))).thenReturn(60L);
        when(sessionRepository.countSessionsByUserSince(eq(user), any(LocalDateTime.class))).thenReturn(3L);
        when(sessionRepository.findByUser(user)).thenReturn(Arrays.asList());

        // When
        FolderResponse result = folderService.updateFolder(1L, request, user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Mathematics");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getTotalHours()).isEqualTo(1L);
        assertThat(result.getSessionCount()).isEqualTo(3L);
        
        verify(folderRepository).findByIdAndUser(1L, user);
        verify(folderRepository).save(any(Folder.class));
        verify(sessionRepository, atLeastOnce()).totalMinutesByUserSince(eq(user), any(LocalDateTime.class));
        verify(sessionRepository, atLeastOnce()).countSessionsByUserSince(eq(user), any(LocalDateTime.class));
        verify(sessionRepository, atLeastOnce()).findByUser(user);
    }

    @Test
    @DisplayName("Should throw exception when updating folder not found")
    void shouldThrowExceptionWhenUpdatingFolderNotFound() {
        // Given
        User user = new User();
        user.setId(1L);

        FolderRequest request = new FolderRequest();
        request.setName("Updated Name");
        request.setDescription("Updated description");

        when(folderRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> folderService.updateFolder(1L, request, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Folder not found");
        
        verify(folderRepository).findByIdAndUser(1L, user);
        verify(folderRepository, never()).save(any(Folder.class));
    }

    @Test
    @DisplayName("Should delete folder successfully")
    void shouldDeleteFolderSuccessfully() {
        // Given
        User user = new User();
        user.setId(1L);

        when(folderRepository.existsByIdAndUser(1L, user)).thenReturn(true);

        // When
        folderService.deleteFolder(1L, user);

        // Then
        verify(folderRepository).existsByIdAndUser(1L, user);
        verify(folderRepository).deleteByIdAndUser(1L, user);
    }

    @Test
    @DisplayName("Should throw exception when deleting folder not found")
    void shouldThrowExceptionWhenDeletingFolderNotFound() {
        // Given
        User user = new User();
        user.setId(1L);

        when(folderRepository.existsByIdAndUser(1L, user)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> folderService.deleteFolder(1L, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Folder not found");
        
        verify(folderRepository).existsByIdAndUser(1L, user);
        verify(folderRepository, never()).deleteByIdAndUser(any(Long.class), any(User.class));
    }
}
