package com.studytracker.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SessionResponse {
    
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Long folderId;
    private String folderName;
    private List<String> tags;
    private LocalDateTime createdAt;
    
    public SessionResponse(Long id, String title, String description, LocalDateTime startTime, LocalDateTime endTime, Integer durationMinutes, Long folderId, String folderName, List<String> tags, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.folderId = folderId;
        this.folderName = folderName;
        this.tags = tags;
        this.createdAt = createdAt;
    }
}
