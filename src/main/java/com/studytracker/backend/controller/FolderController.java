package com.studytracker.backend.controller;

import com.studytracker.backend.dto.FolderRequest;
import com.studytracker.backend.dto.FolderResponse;
import com.studytracker.backend.model.AppUser;
import com.studytracker.backend.service.FolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.studytracker.backend.security.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {
    
    private final FolderService folderService;
    
    @PostMapping
    public ResponseEntity<FolderResponse> createFolder(@Valid @RequestBody FolderRequest request, 
                                               Authentication authentication) {
        //AppUser user = (AppUser) authentication.getPrincipal();
        
        // 1. Primero casteamos al CustomUserDetails (que es lo que Spring Security maneja internamente)
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // 2. Luego extraemos tu AppUser limpio desde adentro
        AppUser user = userDetails.getUser();
        
        FolderResponse response = folderService.createFolder(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<FolderResponse>> getUserFolders(Authentication authentication) {
        //AppUser user = (AppUser) authentication.getPrincipal();
        
        // 1. Primero casteamos al CustomUserDetails (que es lo que Spring Security maneja internamente)
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // 2. Luego extraemos tu AppUser limpio desde adentro
        AppUser user = userDetails.getUser();
        
        List<FolderResponse> folders = folderService.getUserFolders(user);
        return ResponseEntity.ok(folders);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FolderResponse> getFolderById(@PathVariable Long id, 
                                                  Authentication authentication) {
        //AppUser user = (AppUser) authentication.getPrincipal();
        
        // 1. Primero casteamos al CustomUserDetails (que es lo que Spring Security maneja internamente)
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // 2. Luego extraemos tu AppUser limpio desde adentro
        AppUser user = userDetails.getUser();
        
        FolderResponse folder = folderService.getFolderById(id, user);
        return ResponseEntity.ok(folder);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FolderResponse> updateFolder(@PathVariable Long id, 
                                                  @Valid @RequestBody FolderRequest request,
                                                  Authentication authentication) {
        //AppUser user = (AppUser) authentication.getPrincipal();
        
        // 1. Primero casteamos al CustomUserDetails (que es lo que Spring Security maneja internamente)
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // 2. Luego extraemos tu AppUser limpio desde adentro
        AppUser user = userDetails.getUser();
        
        FolderResponse folder = folderService.updateFolder(id, request, user);
        return ResponseEntity.ok(folder);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id, 
                                          Authentication authentication) {
        //AppUser user = (AppUser) authentication.getPrincipal();
        
        // 1. Primero casteamos al CustomUserDetails (que es lo que Spring Security maneja internamente)
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // 2. Luego extraemos tu AppUser limpio desde adentro
        AppUser user = userDetails.getUser();
        
        folderService.deleteFolder(id, user);
        return ResponseEntity.noContent().build();
    }
}
