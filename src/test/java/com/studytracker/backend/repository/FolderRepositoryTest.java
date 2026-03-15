package com.studytracker.backend.repository;

import com.studytracker.backend.model.Folder;
import com.studytracker.backend.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Folder Repository Tests")
class FolderRepositoryTest {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should save and find folder")
    void shouldSaveAndFindFolder() {
        // Given
        User user = new User("John Doe", "john@example.com", "password123");
        entityManager.persistAndFlush(user);

        Folder folder = new Folder("Mathematics", "Study folder for math", user);

        // When
        Folder savedFolder = folderRepository.save(folder);
        entityManager.flush();

        // Then
        assertThat(savedFolder.getId()).isNotNull();
        assertThat(savedFolder.getName()).isEqualTo("Mathematics");
        assertThat(savedFolder.getDescription()).isEqualTo("Study folder for math");
        assertThat(savedFolder.getUser().getId()).isEqualTo(user.getId());
        assertThat(savedFolder.getCreatedAt()).isNotNull();

        // Verify we can find it
        Optional<Folder> foundFolder = folderRepository.findById(savedFolder.getId());
        assertThat(foundFolder).isPresent();
        assertThat(foundFolder.get().getName()).isEqualTo("Mathematics");
        assertThat(foundFolder.get().getDescription()).isEqualTo("Study folder for math");
        assertThat(foundFolder.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("Should find folders by user")
    void shouldFindFoldersByUser() {
        // Given
        User user1 = new User("John Doe", "john@example.com", "password123");
        User user2 = new User("Jane Smith", "jane@example.com", "password456");
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Folder folder1 = new Folder("Mathematics", "Math studies", user1);
        Folder folder2 = new Folder("Physics", "Physics studies", user1);
        Folder folder3 = new Folder("Chemistry", "Chem studies", user2);
        entityManager.persistAndFlush(folder1);
        entityManager.persistAndFlush(folder2);
        entityManager.persistAndFlush(folder3);

        // When
        List<Folder> user1Folders = folderRepository.findByUser(user1);
        List<Folder> user2Folders = folderRepository.findByUser(user2);

        // Then
        assertThat(user1Folders).hasSize(2);
        assertThat(user1Folders)
                .extracting(Folder::getName)
                .containsExactlyInAnyOrder("Mathematics", "Physics");
        
        assertThat(user2Folders).hasSize(1);
        assertThat(user2Folders.get(0).getName()).isEqualTo("Chemistry");
        
        // Verify all folders belong to correct users
        assertThat(user1Folders).allMatch(folder -> folder.getUser().getId().equals(user1.getId()));
        assertThat(user2Folders).allMatch(folder -> folder.getUser().getId().equals(user2.getId()));
    }

    @Test
    @DisplayName("Should find folder by id and user")
    void shouldFindByIdAndUser() {
        // Given
        User user1 = new User("John Doe", "john@example.com", "password123");
        User user2 = new User("Jane Smith", "jane@example.com", "password456");
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Folder folder1 = new Folder("Mathematics", "Math studies", user1);
        Folder folder2 = new Folder("Physics", "Physics studies", user2);
        entityManager.persistAndFlush(folder1);
        entityManager.persistAndFlush(folder2);

        // When
        Optional<Folder> foundByUser1 = folderRepository.findByIdAndUser(folder1.getId(), user1);
        Optional<Folder> foundByUser2 = folderRepository.findByIdAndUser(folder1.getId(), user2);

        // Then
        assertThat(foundByUser1).isPresent();
        assertThat(foundByUser1.get().getName()).isEqualTo("Mathematics");
        assertThat(foundByUser1.get().getUser().getId()).isEqualTo(user1.getId());

        // User2 should not find User1's folder
        assertThat(foundByUser2).isEmpty();

        // But User2 should find their own folder
        Optional<Folder> user2OwnFolder = folderRepository.findByIdAndUser(folder2.getId(), user2);
        assertThat(user2OwnFolder).isPresent();
        assertThat(user2OwnFolder.get().getName()).isEqualTo("Physics");
    }

    @Test
    @DisplayName("Should find folders by user and name containing")
    void shouldFindByUserAndNameContaining() {
        // Given
        User user = new User("John Doe", "john@example.com", "password123");
        entityManager.persistAndFlush(user);

        Folder folder1 = new Folder("Advanced Mathematics", "Advanced math topics", user);
        Folder folder2 = new Folder("Basic Physics", "Basic physics", user);
        Folder folder3 = new Folder("Chemistry Lab", "Chem experiments", user);
        entityManager.persistAndFlush(folder1);
        entityManager.persistAndFlush(folder2);
        entityManager.persistAndFlush(folder3);

        // When
        List<Folder> mathFolders = folderRepository.findByUserAndNameContaining(user, "math");
        List<Folder> physicsFolders = folderRepository.findByUserAndNameContaining(user, "physics");
        List<Folder> nonExistent = folderRepository.findByUserAndNameContaining(user, "biology");

        // Then
        assertThat(mathFolders).hasSize(1);
        assertThat(mathFolders.get(0).getName()).isEqualTo("Advanced Mathematics");

        assertThat(physicsFolders).hasSize(1);
        assertThat(physicsFolders.get(0).getName()).isEqualTo("Basic Physics");

        assertThat(nonExistent).isEmpty();
    }

    @Test
    @DisplayName("Should check if folder exists by id and user")
    void shouldExistsByIdAndUser() {
        // Given
        User user1 = new User("John Doe", "john@example.com", "password123");
        User user2 = new User("Jane Smith", "jane@example.com", "password456");
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Folder folder = new Folder("Mathematics", "Math studies", user1);
        entityManager.persistAndFlush(folder);

        // When
        boolean existsForUser1 = folderRepository.existsByIdAndUser(folder.getId(), user1);
        boolean existsForUser2 = folderRepository.existsByIdAndUser(folder.getId(), user2);
        boolean nonExistentExists = folderRepository.existsByIdAndUser(999L, user1);

        // Then
        assertThat(existsForUser1).isTrue();
        assertThat(existsForUser2).isFalse();
        assertThat(nonExistentExists).isFalse();
    }

    @Test
    @DisplayName("Should delete folder by id and user")
    void shouldDeleteByIdAndUser() {
        // Given
        User user1 = new User("John Doe", "john@example.com", "password123");
        User user2 = new User("Jane Smith", "jane@example.com", "password456");
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Folder folder1 = new Folder("Mathematics", "Math studies", user1);
        Folder folder2 = new Folder("Physics", "Physics studies", user2);
        entityManager.persistAndFlush(folder1);
        entityManager.persistAndFlush(folder2);

        Long folder1Id = folder1.getId();
        Long folder2Id = folder2.getId();

        // When
        folderRepository.deleteByIdAndUser(folder1Id, user1);
        entityManager.flush();

        // Then
        Optional<Folder> deletedFolder = folderRepository.findById(folder1Id);
        assertThat(deletedFolder).isEmpty();

        // User2's folder should still exist
        Optional<Folder> user2Folder = folderRepository.findById(folder2Id);
        assertThat(user2Folder).isPresent();
    }

    @Test
    @DisplayName("Should find folders by user id")
    void shouldFindByUserId() {
        // Given
        User user1 = new User("John Doe", "john@example.com", "password123");
        User user2 = new User("Jane Smith", "jane@example.com", "password456");
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Folder folder1 = new Folder("Mathematics", "Math studies", user1);
        Folder folder2 = new Folder("Physics", "Physics studies", user1);
        Folder folder3 = new Folder("Chemistry", "Chem studies", user2);
        entityManager.persistAndFlush(folder1);
        entityManager.persistAndFlush(folder2);
        entityManager.persistAndFlush(folder3);

        // When
        List<Folder> user1Folders = folderRepository.findByUserId(user1.getId());
        List<Folder> user2Folders = folderRepository.findByUserId(user2.getId());

        // Then
        assertThat(user1Folders).hasSize(2);
        assertThat(user1Folders)
                .extracting(Folder::getName)
                .containsExactlyInAnyOrder("Mathematics", "Physics");
        
        assertThat(user2Folders).hasSize(1);
        assertThat(user2Folders.get(0).getName()).isEqualTo("Chemistry");
    }
}
