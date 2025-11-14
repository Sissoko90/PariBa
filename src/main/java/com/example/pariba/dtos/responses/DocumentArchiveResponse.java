package com.example.pariba.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour les documents archivés
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentArchiveResponse {
    
    /**
     * Identifiant unique du document
     */
    private String id;
    
    /**
     * Type de document (RECEIPT, EXPORT_REPORT, ID_CARD, GROUP_RULES)
     */
    private String documentType;
    
    /**
     * Nom du fichier
     */
    private String fileName;
    
    /**
     * URL de téléchargement du document
     */
    private String downloadUrl;
    
    /**
     * Taille du fichier en octets
     */
    private Long fileSize;
    
    /**
     * Type MIME du fichier
     */
    private String mimeType;
    
    /**
     * Identifiant du groupe associé
     */
    private String groupId;
    
    /**
     * Nom du groupe
     */
    private String groupName;
    
    /**
     * Métadonnées supplémentaires (JSON)
     */
    private String metadata;
    
    /**
     * Date de création
     */
    private LocalDateTime createdAt;
    
    /**
     * Date d'expiration (optionnel)
     */
    private LocalDateTime expiresAt;
}
