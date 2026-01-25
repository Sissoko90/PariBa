package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.GuideResponse;
import com.example.pariba.enums.GuideCategory;
import com.example.pariba.services.IGuideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support/guides")
@Tag(name = "Guides", description = "Gestion des guides d'utilisation")
@SecurityRequirement(name = "bearerAuth")
public class GuideController {
    
    private final IGuideService guideService;
    
    public GuideController(IGuideService guideService) {
        this.guideService = guideService;
    }
    
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Liste des guides", description = "Récupérer tous les guides actifs")
    public ResponseEntity<ApiResponse<List<GuideResponse>>> getActiveGuides() {
        try {
            List<GuideResponse> guides = guideService.getActiveGuides();
            return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, guides));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Guides par catégorie", description = "Récupérer les guides d'une catégorie")
    public ResponseEntity<ApiResponse<List<GuideResponse>>> getGuidesByCategory(@PathVariable GuideCategory category) {
        try {
            List<GuideResponse> guides = guideService.getGuidesByCategory(category);
            return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, guides));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Détails d'un guide", description = "Récupérer les détails d'un guide")
    public ResponseEntity<ApiResponse<GuideResponse>> getGuide(@PathVariable String id) {
        try {
            GuideResponse guide = guideService.getGuideById(id);
            guideService.incrementViewCount(id);
            return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, guide));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Guide non trouvé", null));
        }
    }
}
