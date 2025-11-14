package com.example.pariba.services;

import com.example.pariba.exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Service de validation des fichiers uploadés
 * Protège contre l'upload de fichiers malveillants
 */
@Slf4j
@Service
public class FileValidationService {

    // Extensions autorisées
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        "jpg", "jpeg", "png", "gif", "bmp",  // Images
        "pdf",                                 // Documents
        "doc", "docx",                        // Word
        "xls", "xlsx",                        // Excel
        "txt", "csv"                          // Texte
    );

    // Types MIME autorisés
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/bmp",
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain", "text/csv"
    );

    // Taille maximale: 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    // Signatures de fichiers (magic numbers) pour validation approfondie
    private static final byte[] PDF_SIGNATURE = {0x25, 0x50, 0x44, 0x46}; // %PDF
    private static final byte[] JPEG_SIGNATURE = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 0x50, 0x4E, 0x47};

    /**
     * Valide un fichier uploadé
     * @throws BadRequestException si le fichier est invalide
     */
    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Le fichier est vide ou manquant");
        }

        // 1. Vérifier la taille
        validateFileSize(file);

        // 2. Vérifier l'extension
        String filename = file.getOriginalFilename();
        validateFileExtension(filename);

        // 3. Vérifier le type MIME
        validateMimeType(file.getContentType());

        // 4. Vérifier la signature du fichier (magic number)
        try {
            validateFileSignature(file);
        } catch (IOException e) {
            log.error("Erreur lors de la validation de la signature du fichier", e);
            throw new BadRequestException("Impossible de valider le fichier");
        }

        log.info("Fichier validé avec succès: {}", filename);
    }

    /**
     * Valide la taille du fichier
     */
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException(
                String.format("Le fichier est trop volumineux. Taille maximale: %d MB", 
                    MAX_FILE_SIZE / (1024 * 1024))
            );
        }

        if (file.getSize() == 0) {
            throw new BadRequestException("Le fichier est vide");
        }
    }

    /**
     * Valide l'extension du fichier
     */
    private void validateFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new BadRequestException("Nom de fichier invalide");
        }

        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(
                "Type de fichier non autorisé. Extensions autorisées: " + 
                String.join(", ", ALLOWED_EXTENSIONS)
            );
        }
    }

    /**
     * Valide le type MIME
     */
    private void validateMimeType(String mimeType) {
        if (mimeType == null || mimeType.isEmpty()) {
            throw new BadRequestException("Type MIME manquant");
        }

        if (!ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase())) {
            throw new BadRequestException("Type MIME non autorisé: " + mimeType);
        }
    }

    /**
     * Valide la signature du fichier (magic number)
     * Vérifie que le contenu réel correspond à l'extension
     */
    private void validateFileSignature(MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        
        if (fileBytes.length < 4) {
            throw new BadRequestException("Fichier trop petit pour être valide");
        }

        String mimeType = file.getContentType();
        
        // Vérifier PDF
        if (mimeType != null && mimeType.equals("application/pdf")) {
            if (!startsWith(fileBytes, PDF_SIGNATURE)) {
                throw new BadRequestException("Le fichier n'est pas un PDF valide");
            }
        }
        
        // Vérifier JPEG
        if (mimeType != null && mimeType.equals("image/jpeg")) {
            if (!startsWith(fileBytes, JPEG_SIGNATURE)) {
                throw new BadRequestException("Le fichier n'est pas un JPEG valide");
            }
        }
        
        // Vérifier PNG
        if (mimeType != null && mimeType.equals("image/png")) {
            if (!startsWith(fileBytes, PNG_SIGNATURE)) {
                throw new BadRequestException("Le fichier n'est pas un PNG valide");
            }
        }
    }

    /**
     * Vérifie si un tableau de bytes commence par une signature donnée
     */
    private boolean startsWith(byte[] array, byte[] signature) {
        if (array.length < signature.length) {
            return false;
        }
        
        for (int i = 0; i < signature.length; i++) {
            if (array[i] != signature[i]) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Génère un nom de fichier sécurisé (sans caractères spéciaux)
     */
    public String sanitizeFilename(String filename) {
        if (filename == null) {
            return "file";
        }
        
        // Garder seulement les caractères alphanumériques, tirets, underscores et points
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
