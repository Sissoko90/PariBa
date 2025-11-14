package com.example.pariba.services;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service pour la gestion du stockage de fichiers
 */
public interface IFileStorageService {
    
    /**
     * Sauvegarder un fichier
     * @param file Fichier à sauvegarder
     * @param directory Répertoire de destination (ex: "profiles", "documents")
     * @return URL ou chemin du fichier sauvegardé
     */
    String saveFile(MultipartFile file, String directory);
    
    /**
     * Supprimer un fichier
     * @param filePath Chemin du fichier
     */
    void deleteFile(String filePath);
    
    /**
     * Vérifier si un fichier existe
     * @param filePath Chemin du fichier
     * @return true si le fichier existe
     */
    boolean fileExists(String filePath);
    
    /**
     * Obtenir l'URL complète d'un fichier
     * @param filePath Chemin relatif du fichier
     * @return URL complète
     */
    String getFileUrl(String filePath);
}
