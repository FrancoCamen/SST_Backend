package com.studytracker.backend.controller;

import com.studytracker.backend.dto.SessionRequest;
import com.studytracker.backend.dto.SessionResponse;
import com.studytracker.backend.model.AppUser;
import com.studytracker.backend.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class SessionController {
    
    private final SessionService sessionService;
    
    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody SessionRequest request, 
                                                  Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
        SessionResponse response = sessionService.createSession(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<SessionResponse>> getUserSessions(Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
        List<SessionResponse> sessions = sessionService.getUserSessions(user);
        return ResponseEntity.ok(sessions);
    }
    
    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<SessionResponse>> getSessionsByFolder(@PathVariable Long folderId, 
                                                                Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
        List<SessionResponse> sessions = sessionService.getUserSessionsByFolder(folderId, user);
        return ResponseEntity.ok(sessions);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> getSessionById(@PathVariable Long id, 
                                                    Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
        SessionResponse session = sessionService.getSessionById(id, user);
        return ResponseEntity.ok(session);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SessionResponse> updateSession(@PathVariable Long id, 
                                                  @Valid @RequestBody SessionRequest request,
                                                  Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
        SessionResponse session = sessionService.updateSession(id, request, user);
        return ResponseEntity.ok(session);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id, 
                                          Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
        sessionService.deleteSession(id, user);
        return ResponseEntity.noContent().build();
    }
}
