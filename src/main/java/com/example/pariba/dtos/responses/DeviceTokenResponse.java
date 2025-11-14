package com.example.pariba.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour les tokens d'appareil (push notifications)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTokenResponse {
    
    /**
     * Identifiant unique du token
     */
    private String id;
    
    /**
     * Token FCM/APNs
     */
    private String token;
    
    /**
     * Plateforme (ios, android, web)
     */
    private String platform;
    
    /**
     * Identifiant de la personne propriétaire
     */
    private String personId;
    
    /**
     * Indique si le token est actif
     */
    private Boolean active;
    
    /**
     * Date de création
     */
    private LocalDateTime createdAt;
    
    /**
     * Date de dernière utilisation
     */
    private LocalDateTime lastUsedAt;
}
