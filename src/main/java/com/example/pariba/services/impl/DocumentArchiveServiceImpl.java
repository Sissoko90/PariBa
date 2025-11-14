package com.example.pariba.services.impl;

import com.example.pariba.dtos.responses.DocumentArchiveResponse;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.DocumentArchive;
import com.example.pariba.models.TontineGroup;
import com.example.pariba.repositories.DocumentArchiveRepository;
import com.example.pariba.repositories.TontineGroupRepository;
import com.example.pariba.services.IDocumentArchiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des documents archivés
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentArchiveServiceImpl implements IDocumentArchiveService {
    
    private final DocumentArchiveRepository documentArchiveRepository;
    private final TontineGroupRepository tontineGroupRepository;
    
    @Override
    @Transactional
    public DocumentArchiveResponse archiveDocument(String groupId, String documentType, 
                                                   String fileName, String downloadUrl, 
                                                   Long fileSize, String metadata) {
        log.info("Archivage du document {} pour le groupe: {}", fileName, groupId);
        
        TontineGroup group = tontineGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Groupe non trouvé"));
        
        DocumentArchive document = new DocumentArchive();
        document.setGroup(group);
        document.setType(com.example.pariba.enums.DocumentType.valueOf(documentType));
        document.setFileName(fileName);
        document.setDownloadUrl(downloadUrl);
        document.setFileSize(fileSize);
        document.setMetadata(metadata);
        
        // Déterminer le type MIME basé sur l'extension
        String mimeType = determineMimeType(fileName);
        document.setMimeType(mimeType);
        
        document = documentArchiveRepository.save(document);
        log.info("Document archivé avec succès: {}", document.getId());
        
        return mapToResponse(document);
    }
    
    @Override
    public DocumentArchiveResponse getDocumentById(String documentId) {
        log.info("Récupération du document: {}", documentId);
        
        DocumentArchive document = documentArchiveRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document non trouvé"));
        
        return mapToResponse(document);
    }
    
    @Override
    public List<DocumentArchiveResponse> getDocumentsByGroup(String groupId) {
        log.info("Récupération des documents du groupe: {}", groupId);
        
        TontineGroup group = tontineGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Groupe non trouvé"));
        
        List<DocumentArchive> documents = documentArchiveRepository.findByGroup(group);
        
        return documents.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<DocumentArchiveResponse> getDocumentsByGroupAndType(String groupId, String documentType) {
        log.info("Récupération des documents du groupe: {} de type: {}", groupId, documentType);
        
        TontineGroup group = tontineGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Groupe non trouvé"));
        
        com.example.pariba.enums.DocumentType type = com.example.pariba.enums.DocumentType.valueOf(documentType);
        List<DocumentArchive> documents = documentArchiveRepository
                .findByGroupAndType(group, type);
        
        return documents.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteDocument(String documentId, String personId) {
        log.info("Suppression du document: {} par person: {}", documentId, personId);
        
        DocumentArchive document = documentArchiveRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document non trouvé"));
        
        // TODO: Vérifier les droits de la personne sur le groupe
        // Pour l'instant, on autorise la suppression
        
        documentArchiveRepository.delete(document);
        log.info("Document supprimé avec succès");
    }
    
    /**
     * Détermine le type MIME basé sur l'extension du fichier
     */
    private String determineMimeType(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }
        
        String lowerFileName = fileName.toLowerCase();
        if (lowerFileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerFileName.endsWith(".xlsx") || lowerFileName.endsWith(".xls")) {
            return "application/vnd.ms-excel";
        } else if (lowerFileName.endsWith(".csv")) {
            return "text/csv";
        } else if (lowerFileName.endsWith(".png")) {
            return "image/png";
        } else if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else {
            return "application/octet-stream";
        }
    }
    
    /**
     * Convertit une entité DocumentArchive en DocumentArchiveResponse
     */
    private DocumentArchiveResponse mapToResponse(DocumentArchive document) {
        return DocumentArchiveResponse.builder()
                .id(document.getId())
                .documentType(document.getType().name())
                .fileName(document.getFileName())
                .downloadUrl(document.getDownloadUrl())
                .fileSize(document.getFileSize())
                .mimeType(document.getMimeType())
                .groupId(document.getGroup().getId())
                .groupName(document.getGroup().getNom())
                .metadata(document.getMetadata())
                .createdAt(document.getCreatedAt() != null ? java.time.LocalDateTime.ofInstant(document.getCreatedAt(), java.time.ZoneId.systemDefault()) : null)
                .expiresAt(document.getExpiresAt() != null ? java.time.LocalDateTime.ofInstant(document.getExpiresAt(), java.time.ZoneId.systemDefault()) : null)
                .build();
    }
}
