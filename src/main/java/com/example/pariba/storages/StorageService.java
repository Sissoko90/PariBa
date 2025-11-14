package com.example.pariba.storages;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    
    /**
     * Stocke un fichier et retourne l'URL
     */
    String store(MultipartFile file, String folder);
    
    /**
     * Supprime un fichier
     */
    void delete(String fileUrl);
    
    /**
     * Vérifie si un fichier existe
     */
    boolean exists(String fileUrl);
    
    /**
     * Récupère le chemin complet d'un fichier
     */
    String getFullPath(String fileUrl);
}
