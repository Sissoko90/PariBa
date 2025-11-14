package com.example.pariba.storages;

import com.example.pariba.constants.AppConstants;
import com.example.pariba.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    @Value("${app.storage.location:uploads}")
    private String storageLocation;

    @Value("${app.storage.base-url:http://localhost:8080/uploads}")
    private String baseUrl;

    @Override
    public String store(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new BadRequestException("Le fichier est vide");
        }

        // Vérifier la taille du fichier
        if (file.getSize() > AppConstants.MAX_FILE_SIZE) {
            throw new BadRequestException("Le fichier est trop volumineux (max 10MB)");
        }

        // Vérifier le type de fichier
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new BadRequestException("Type de fichier non autorisé");
        }

        try {
            // Créer le dossier si nécessaire
            Path folderPath = Paths.get(storageLocation, folder);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
            String filename = UUID.randomUUID().toString() + extension;

            // Sauvegarder le fichier
            Path destinationFile = folderPath.resolve(filename);
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Retourner l'URL
            return baseUrl + "/" + folder + "/" + filename;

        } catch (IOException e) {
            throw new BadRequestException("Erreur lors de la sauvegarde du fichier", e);
        }
    }

    @Override
    public void delete(String fileUrl) {
        try {
            String relativePath = fileUrl.replace(baseUrl + "/", "");
            Path filePath = Paths.get(storageLocation, relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new BadRequestException("Erreur lors de la suppression du fichier", e);
        }
    }

    @Override
    public boolean exists(String fileUrl) {
        String relativePath = fileUrl.replace(baseUrl + "/", "");
        Path filePath = Paths.get(storageLocation, relativePath);
        return Files.exists(filePath);
    }

    @Override
    public String getFullPath(String fileUrl) {
        String relativePath = fileUrl.replace(baseUrl + "/", "");
        return Paths.get(storageLocation, relativePath).toString();
    }

    private boolean isAllowedContentType(String contentType) {
        for (String allowedType : AppConstants.ALLOWED_IMAGE_TYPES) {
            if (contentType.equals(allowedType)) {
                return true;
            }
        }
        for (String allowedType : AppConstants.ALLOWED_DOCUMENT_TYPES) {
            if (contentType.equals(allowedType)) {
                return true;
            }
        }
        return false;
    }
}
