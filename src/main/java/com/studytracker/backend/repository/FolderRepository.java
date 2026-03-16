package com.studytracker.backend.repository;

import com.studytracker.backend.model.Folder;
import com.studytracker.backend.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    
    List<Folder> findByUser(AppUser user);
    
    List<Folder> findByUserId(Long userId);
    
    Optional<Folder> findByIdAndUser(Long id, AppUser user);    
    
    @Query("SELECT f FROM Folder f WHERE f.user = :user AND LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Folder> findByUserAndNameContaining(@Param("user") AppUser user, @Param("name") String name);
    
    void deleteByIdAndUser(Long id, AppUser user);
    
    boolean existsByIdAndUser(Long id, AppUser user);
}
