package com.example.pariba.services.impl;

import com.example.pariba.exceptions.BadRequestException;
import com.example.pariba.services.IFileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Implémentation du service de stockage de fichiers
 */
@Service
@Slf4j
public class FileStorageServiceImpl implements IFileStorageService {
    
    @Value("${file.upload.dir:./uploads}")
    private String uploadDir;
    
    @Value("${server.port:8081}")
    private String serverPort;
    
    // Extensions autorisées pour les images
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
        "jpg", "jpeg", "png", "gif", "webp", "bmp"
    );
    
    // Taille maximale: 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    
    @Override
    public String saveFile(MultipartFile file, String directory) {
        if (file.isEmpty()) {
            throw new BadRequestException("Le fichier est vide");
        }
        
        // Vérifier la taille
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("Le fichier est trop volumineux. Maximum: 5MB");
        }
        
        // Vérifier l'extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BadRequestException("Nom de fichier invalide");
        }
        
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("Type de fichier non autorisé. Formats acceptés: " + ALLOWED_IMAGE_EXTENSIONS);
        }
        
        try {
            // Créer le répertoire s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir, directory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Générer un nom unique
            String filename = UUID.randomUUID().toString() + "." + extension;
            Path filePath = uploadPath.resolve(filename);
            
            // Copier le fichier
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Retourner le chemin relatif
            String relativePath = directory + "/" + filename;
            log.info("✅ Fichier sauvegardé: {}", relativePath);
            
            return relativePath;
            
        } catch (IOException e) {
            log.error("❌ Erreur lors de la sauvegarde du fichier: {}", e.getMessage());
            throw new BadRequestException("Erreur lors de la sauvegarde du fichier: " + e.getMessage());
        }
    }
    
    @Override
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }
        
        try {
            Path path = Paths.get(uploadDir, filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("✅ Fichier supprimé: {}", filePath);
            }
        } catch (IOException e) {
            log.error("❌ Erreur lors de la suppression du fichier: {}", e.getMessage());
        }
    }
    
    @Override
    public boolean fileExists(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        Path path = Paths.get(uploadDir, filePath);
        return Files.exists(path);
    }
    
    @Override
    public String getFileUrl(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        
        // Si c'est déjà une URL complète, la retourner
        if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
            return filePath;
        }
        
        // Construire l'URL
        return "http://localhost:" + serverPort + "/uploads/" + filePath;
    }
    
    /**
     * Extraire l'extension d'un fichier
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
