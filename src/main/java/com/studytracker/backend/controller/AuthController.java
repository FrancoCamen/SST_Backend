package com.studytracker.backend.controller;

import com.studytracker.backend.dto.AuthRequest;
import com.studytracker.backend.dto.AuthResponse;
import com.studytracker.backend.dto.RegisterRequest;
import com.studytracker.backend.model.AppUser;
import com.studytracker.backend.security.CustomUserDetails;
import com.studytracker.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    //@GetMapping("/test")
    //public ResponseEntity<String> test() {
    //    return ResponseEntity.ok("Auth endpoint is working");
    //}

    @GetMapping("/test") // Puedes renombrarlo a "/me" en un futuro si prefieres
    public ResponseEntity<AuthResponse> test(Authentication authentication) {
        // 1. Verificamos que la petición realmente traiga un token válido
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. Extraemos el usuario exacto de la base de datos a través del contexto de Spring Security
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        AppUser user = userDetails.getUser();
        
        // 3. Armamos la respuesta. 
        // Pasamos null en el token porque el frontend ya tiene el suyo guardado y no hace falta pisarlo.
        AuthResponse response = new AuthResponse(
            null, 
            user.getId(), 
            user.getName(), 
            user.getEmail()
        );
        
        return ResponseEntity.ok(response);
    }
}
