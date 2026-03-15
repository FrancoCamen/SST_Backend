package com.studytracker.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FolderRequest {
    
    @NotBlank(message = "Folder name is required")
    @Size(max = 100, message = "Folder name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
