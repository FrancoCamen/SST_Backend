package com.studytracker.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FolderResponse {
    
    private Long id;
    private String name;
    private String description;
    private Long totalHours;
    private Long sessionCount;
    private LocalDateTime lastSessionDate;
    private LocalDateTime createdAt;
    
    public FolderResponse(Long id, String name, String description, Long totalHours, Long sessionCount, LocalDateTime lastSessionDate, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.totalHours = totalHours;
        this.sessionCount = sessionCount;
        this.lastSessionDate = lastSessionDate;
        this.createdAt = createdAt;
    }
}
