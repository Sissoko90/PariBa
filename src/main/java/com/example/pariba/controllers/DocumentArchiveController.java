package com.example.pariba.controllers;

import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.DocumentArchiveResponse;
import com.example.pariba.services.IDocumentArchiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des documents archivés
 */
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentArchiveController {
    
    private final IDocumentArchiveService documentArchiveService;
    
    /**
     * Récupère un document par son ID
     */
    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<DocumentArchiveResponse>> getDocumentById(
            @PathVariable String documentId) {
        
        DocumentArchiveResponse document = documentArchiveService.getDocumentById(documentId);
        
        return ResponseEntity.ok(ApiResponse.success("Document récupéré avec succès", document));
    }
    
    /**
     * Récupère tous les documents d'un groupe
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<List<DocumentArchiveResponse>>> getDocumentsByGroup(
            @PathVariable String groupId) {
        
        List<DocumentArchiveResponse> documents = documentArchiveService.getDocumentsByGroup(groupId);
        
        return ResponseEntity.ok(ApiResponse.success("Documents récupérés avec succès", documents));
    }
    
    /**
     * Récupère les documents d'un groupe par type
     */
    @GetMapping("/group/{groupId}/type/{documentType}")
    public ResponseEntity<ApiResponse<List<DocumentArchiveResponse>>> getDocumentsByGroupAndType(
            @PathVariable String groupId,
            @PathVariable String documentType) {
        
        List<DocumentArchiveResponse> documents = documentArchiveService
                .getDocumentsByGroupAndType(groupId, documentType);
        
        return ResponseEntity.ok(ApiResponse.success("Documents récupérés avec succès", documents));
    }
    
    /**
     * Supprime un document
     */
    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable String documentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String personId = userDetails.getUsername();
        documentArchiveService.deleteDocument(documentId, personId);
        
        return ResponseEntity.ok(ApiResponse.success("Document supprimé avec succès", null));
    }
}
