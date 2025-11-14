package com.example.pariba.services;

import com.example.pariba.dtos.responses.DocumentArchiveResponse;

import java.util.List;

/**
 * Interface du service de gestion des documents archivés
 */
public interface IDocumentArchiveService {
    
    /**
     * Archive un document
     * @param groupId L'identifiant du groupe
     * @param documentType Le type de document
     * @param fileName Le nom du fichier
     * @param downloadUrl L'URL de téléchargement
     * @param fileSize La taille du fichier
     * @param metadata Les métadonnées (JSON)
     * @return Le document archivé
     */
    DocumentArchiveResponse archiveDocument(String groupId, String documentType, String fileName, 
                                            String downloadUrl, Long fileSize, String metadata);
    
    /**
     * Récupère un document par son ID
     * @param documentId L'identifiant du document
     * @return Le document
     */
    DocumentArchiveResponse getDocumentById(String documentId);
    
    /**
     * Récupère tous les documents d'un groupe
     * @param groupId L'identifiant du groupe
     * @return Liste des documents
     */
    List<DocumentArchiveResponse> getDocumentsByGroup(String groupId);
    
    /**
     * Récupère les documents d'un groupe par type
     * @param groupId L'identifiant du groupe
     * @param documentType Le type de document
     * @return Liste des documents
     */
    List<DocumentArchiveResponse> getDocumentsByGroupAndType(String groupId, String documentType);
    
    /**
     * Supprime un document
     * @param documentId L'identifiant du document
     * @param personId L'identifiant de la personne (pour vérification des droits)
     */
    void deleteDocument(String documentId, String personId);
}
