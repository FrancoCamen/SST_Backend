package com.studytracker.backend.repository;

import com.studytracker.backend.model.Folder;
import com.studytracker.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    
    List<Folder> findByUser(User user);
    
    List<Folder> findByUserId(Long userId);
    
    Optional<Folder> findByIdAndUser(Long id, User user);
    
    @Query("SELECT f FROM Folder f WHERE f.user = :user AND LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Folder> findByUserAndNameContaining(@Param("user") User user, @Param("name") String name);
    
    void deleteByIdAndUser(Long id, User user);
    
    boolean existsByIdAndUser(Long id, User user);
}
