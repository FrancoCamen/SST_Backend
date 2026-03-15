package com.studytracker.backend.repository;

import com.studytracker.backend.model.Session;
import com.studytracker.backend.model.User;
import com.studytracker.backend.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    
    List<Session> findByUser(User user);
    
    List<Session> findByUserId(Long userId);
    
    List<Session> findByUserAndFolder(User user, Folder folder);
    
    Optional<Session> findByIdAndUser(Long id, User user);
    
    @Query("SELECT s FROM Session s WHERE s.user = :user AND s.startTime >= :startDate AND s.endTime <= :endDate")
    List<Session> findByUserAndDateRange(@Param("user") User user, 
                                        @Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT s FROM Session s WHERE s.user = :user AND LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Session> findByUserAndTitleContaining(@Param("user") User user, @Param("title") String title);
    
    @Query("SELECT COUNT(s) FROM Session s WHERE s.user = :user AND s.startTime >= :startDate")
    Long countSessionsByUserSince(@Param("user") User user, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT SUM(s.durationMinutes) FROM Session s WHERE s.user = :user AND s.startTime >= :startDate")
    Long totalMinutesByUserSince(@Param("user") User user, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT AVG(s.durationMinutes) FROM Session s WHERE s.user = :user AND s.startTime >= :startDate")
    Double averageDurationByUserSince(@Param("user") User user, @Param("startDate") LocalDateTime startDate);
    
    void deleteByIdAndUser(Long id, User user);
}
