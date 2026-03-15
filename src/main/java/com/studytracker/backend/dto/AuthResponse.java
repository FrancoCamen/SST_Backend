package com.studytracker.backend.dto;

import lombok.Data;

@Data
public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String name;
    private String email;
    
    public AuthResponse(String token, Long userId, String name, String email) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.email = email;
    }
}
