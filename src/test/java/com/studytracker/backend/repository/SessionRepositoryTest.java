package com.studytracker.backend.repository;

import com.studytracker.backend.model.Folder;
import com.studytracker.backend.model.Session;
import com.studytracker.backend.model.Tag;
import com.studytracker.backend.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Session Repository Tests")
class SessionRepositoryTest {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TestEntityManager entityManager;

    /**
     * Refactorizado: Ahora calcula el endTime basándose en la duración 
     * para mantener la consistencia con @PrePersist de la entidad.
     */
    private Session createValidSession(String title, User user, Folder folder, int duration, LocalDateTime startTime) {
        Session session = new Session();
        session.setTitle(title);
        session.setDescription("Description for " + title);
        session.setStartTime(startTime);
        // El endTime DEBE ser consistente con la duración para evitar errores de cálculo
        session.setEndTime(startTime.plusMinutes(duration));
        session.setDurationMinutes(duration);
        session.setUser(user);
        session.setFolder(folder);
        return session;
    }

    @Test
    @DisplayName("Should save and find session")
    void shouldSaveAndFindSession() {
        User user = new User("John Doe", "john@example.com", "password123");
        entityManager.persistAndFlush(user);

        Folder folder = new Folder("Mathematics", "Math studies", user);
        entityManager.persistAndFlush(folder);

        Tag tag = new Tag("math");
        entityManager.persistAndFlush(tag);

        // Usamos LocalDateTime.now() como inicio por defecto
        Session session = createValidSession("Math Study Session", user, folder, 60, LocalDateTime.now());
        session.setTags(List.of(tag));

        Session savedSession = sessionRepository.save(session);
        entityManager.flush();

        assertThat(savedSession.getId()).isNotNull();
        assertThat(savedSession.getDurationMinutes()).isEqualTo(60);
        assertThat(savedSession.getTags()).hasSize(1);
    }

    @Test
    @DisplayName("Should find sessions by user and folder")
    void shouldFindByUserAndFolder() {
        User user = new User("John Doe", "john@example.com", "password123");
        entityManager.persistAndFlush(user);

        Folder folder = new Folder("Mathematics", "Math studies", user);
        entityManager.persistAndFlush(folder);

        Session session1 = createValidSession("Math Session 1", user, folder, 60, LocalDateTime.now().minusHours(2));
        Session session2 = createValidSession("Math Session 2", user, folder, 45, LocalDateTime.now().minusHours(1));

        entityManager.persistAndFlush(session1);
        entityManager.persistAndFlush(session2);

        List<Session> sessions = sessionRepository.findByUserAndFolder(user, folder);

        assertThat(sessions).hasSize(2);
        assertThat(sessions).extracting(Session::getTitle)
                .containsExactlyInAnyOrder("Math Session 1", "Math Session 2");
    }

    @Test
    @DisplayName("Should find session by id and user")
    void shouldFindByIdAndUser() {
        User user1 = new User("John Doe", "john@example.com", "password123");
        User user2 = new User("Jane Smith", "jane@example.com", "password456");
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        Folder folder = new Folder("General", "Notes", user1);
        entityManager.persistAndFlush(folder);

        Session session1 = createValidSession("User1 Session", user1, folder, 30, LocalDateTime.now());
        entityManager.persistAndFlush(session1);

        Optional<Session> found = sessionRepository.findByIdAndUser(session1.getId(), user1);
        Optional<Session> notFound = sessionRepository.findByIdAndUser(session1.getId(), user2);

        assertThat(found).isPresent();
        assertThat(notFound).isEmpty();
    }

    @Test
    @DisplayName("Should calculate total minutes by user since date")
    void shouldCalculateTotalMinutesByUserSince() {
        User user = new User("John Doe", "john@example.com", "password123");
        entityManager.persistAndFlush(user);

        Folder folder = new Folder("Math", "Studies", user);
        entityManager.persistAndFlush(folder);

        LocalDateTime startDate = LocalDateTime.now().minusDays(1);

        // Sesión RECIENTE: 100 minutos reales (start + 100min = end)
        Session s1 = createValidSession("Recent Session", user, folder, 100, startDate.plusHours(1));
        
        // Sesión ANTIGUA: Fuera del rango de búsqueda
        Session s2 = createValidSession("Old Session", user, folder, 50, startDate.minusDays(2));

        entityManager.persistAndFlush(s1);
        entityManager.persistAndFlush(s2);

        Long totalMinutes = sessionRepository.totalMinutesByUserSince(user, startDate);

        // Ahora el resultado será exactamente 100L
        assertThat(totalMinutes).isEqualTo(100L);
    }

    @Test
    @DisplayName("Should count sessions by user since date")
    void shouldCountSessionsByUserSince() {
        User user = new User("John Doe", "john@example.com", "password123");
        entityManager.persistAndFlush(user);

        Folder folder = new Folder("Math", "Studies", user);
        entityManager.persistAndFlush(folder);

        LocalDateTime startDate = LocalDateTime.now().minusDays(1);

        Session s1 = createValidSession("Session 1", user, folder, 30, startDate.plusHours(1));
        Session s2 = createValidSession("Session 2", user, folder, 30, startDate.minusHours(5));

        entityManager.persistAndFlush(s1);
        entityManager.persistAndFlush(s2);

        Long count = sessionRepository.countSessionsByUserSince(user, startDate);

        assertThat(count).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should find sessions by user and title containing")
    void shouldFindByUserAndTitleContaining() {
        User user = new User("John Doe", "john@example.com", "password123");
        entityManager.persistAndFlush(user);

        Folder folder = new Folder("Math", "Studies", user);
        entityManager.persistAndFlush(folder);

        entityManager.persistAndFlush(createValidSession("Algebra Study", user, folder, 30, LocalDateTime.now()));
        entityManager.persistAndFlush(createValidSession("Geometry Review", user, folder, 30, LocalDateTime.now()));

        List<Session> results = sessionRepository.findByUserAndTitleContaining(user, "Algebra");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Algebra Study");
    }

    @Test
    @DisplayName("Should delete session by id and user")
    void shouldDeleteByIdAndUser() {
        User user = new User("John Doe", "john@example.com", "password123");
        entityManager.persistAndFlush(user);

        Folder folder = new Folder("Math", "Studies", user);
        entityManager.persistAndFlush(folder);

        Session session = createValidSession("To Delete", user, folder, 20, LocalDateTime.now());
        entityManager.persistAndFlush(session);

        Long sessionId = session.getId();
        sessionRepository.deleteByIdAndUser(sessionId, user);
        entityManager.flush();
        entityManager.clear(); // Limpiamos caché para asegurar que la consulta vaya a DB

        assertThat(sessionRepository.findById(sessionId)).isEmpty();
    }
}