package com.studytracker.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir orígenes específicos para producción y desarrollo
        configuration.setAllowedOriginPatterns(List.of(
            "http://localhost:*",
            "https://localhost:*",
            "http://127.0.0.1:*",
            "https://127.0.0.1:*"
        ));
        
        // Permitir métodos HTTP comunes
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Permitir headers comunes incluyendo headers de autorización
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "Accept", "Origin", 
            "X-Requested-With", "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Permitir credentials para cookies y headers de autorización
        configuration.setAllowCredentials(true);
        
        // Exponer headers específicos si es necesario
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type"
        ));
        
        // Tiempo de cache para pre-flight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
