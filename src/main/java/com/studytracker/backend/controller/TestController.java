package com.studytracker.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class TestController {
    
    @GetMapping("/public/health")
    public ResponseEntity<String> publicHealth() {
        return ResponseEntity.ok("Public endpoint is working");
    }
    
    @GetMapping("/protected/user")
    public ResponseEntity<String> protectedUser(Authentication authentication) {
        return ResponseEntity.ok("Hello " + authentication.getName() + "! This is a protected endpoint.");
    }
    
    @GetMapping("/admin/dashboard")
    public ResponseEntity<String> adminDashboard() {
        return ResponseEntity.ok("Admin dashboard - only for authenticated users");
    }
}
