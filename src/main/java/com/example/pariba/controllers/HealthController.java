package com.example.pariba.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller de test pour vérifier que l'API fonctionne
 */
@RestController
public class HealthController {
    
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "message", "API Pariba v1 is running",
            "timestamp", java.time.Instant.now().toString()
        );
    }
}
