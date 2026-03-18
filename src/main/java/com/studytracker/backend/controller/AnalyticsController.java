package com.studytracker.backend.controller;

import com.studytracker.backend.dto.MonthlyAnalyticsResponse;
import com.studytracker.backend.dto.WeeklyAnalyticsResponse;
import com.studytracker.backend.model.AppUser;
import com.studytracker.backend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import com.studytracker.backend.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", ${CORS_ALLOWED_ORIGINS}})
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping("/weekly")
    public ResponseEntity<WeeklyAnalyticsResponse> getWeeklyAnalytics(Authentication authentication) {
        //AppUser user = (AppUser) authentication.getPrincipal();

        // 1. Primero casteamos al CustomUserDetails (que es lo que Spring Security maneja internamente)
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // 2. Luego extraemos tu AppUser limpio desde adentro
        AppUser user = userDetails.getUser();
        WeeklyAnalyticsResponse analytics = analyticsService.getWeeklyAnalytics(user);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/monthly")
    public ResponseEntity<MonthlyAnalyticsResponse> getMonthlyAnalytics(Authentication authentication) {
        //AppUser user = (AppUser) authentication.getPrincipal();
        
        // 1. Primero casteamos al CustomUserDetails (que es lo que Spring Security maneja internamente)
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // 2. Luego extraemos tu AppUser limpio desde adentro
        AppUser user = userDetails.getUser();
        
        MonthlyAnalyticsResponse analytics = analyticsService.getMonthlyAnalytics(user);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/by-folder")
    public ResponseEntity<Map<String, Long>> getHoursByFolder(Authentication authentication) {
        //AppUser user = (AppUser) authentication.getPrincipal();
        
        // 1. Primero casteamos al CustomUserDetails (que es lo que Spring Security maneja internamente)
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // 2. Luego extraemos tu AppUser limpio desde adentro
        AppUser user = userDetails.getUser();
        
        Map<String, Long> hoursByFolder = analyticsService.getHoursByFolder(user);
        return ResponseEntity.ok(hoursByFolder);
    }
    
    @GetMapping("/productivity-hours")
    public ResponseEntity<Map<Integer, Long>> getProductivityHours(Authentication authentication) {
        //AppUser user = (AppUser) authentication.getPrincipal();
        
        // 1. Primero casteamos al CustomUserDetails (que es lo que Spring Security maneja internamente)
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        // 2. Luego extraemos tu AppUser limpio desde adentro
        AppUser user = userDetails.getUser();
        
        Map<Integer, Long> productivityHours = analyticsService.getProductivityHours(user);
        return ResponseEntity.ok(productivityHours);
    }
}
