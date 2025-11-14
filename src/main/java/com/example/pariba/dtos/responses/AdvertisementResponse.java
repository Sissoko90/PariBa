package com.example.pariba.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour les publicités
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementResponse {
    
    /**
     * Identifiant unique de la publicité
     */
    private String id;
    
    /**
     * Titre de la publicité
     */
    private String title;
    
    /**
     * Description/contenu de la publicité
     */
    private String description;
    
    /**
     * URL de l'image
     */
    private String imageUrl;
    
    /**
     * URL de destination (lien cliquable)
     */
    private String linkUrl;
    
    /**
     * Placement de la publicité (banner, interstitial, native)
     */
    private String placement;
    
    /**
     * Critères de ciblage (JSON)
     */
    private String targetingCriteria;
    
    /**
     * Indique si la publicité est active
     */
    private Boolean active;
    
    /**
     * Nombre d'impressions
     */
    private Long impressions;
    
    /**
     * Nombre de clics
     */
    private Long clicks;
    
    /**
     * Date de début de diffusion
     */
    private LocalDateTime startDate;
    
    /**
     * Date de fin de diffusion
     */
    private LocalDateTime endDate;
    
    /**
     * Date de création
     */
    private LocalDateTime createdAt;
}
