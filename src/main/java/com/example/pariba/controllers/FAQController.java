package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.FAQResponse;
import com.example.pariba.enums.FAQCategory;
import com.example.pariba.services.IFAQService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support/faqs")
@Tag(name = "FAQ", description = "Gestion des questions fréquentes")
@SecurityRequirement(name = "bearerAuth")
public class FAQController {
    
    private final IFAQService faqService;
    
    public FAQController(IFAQService faqService) {
        this.faqService = faqService;
    }
    
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Liste des FAQs", description = "Récupérer toutes les FAQs actives")
    public ResponseEntity<ApiResponse<List<FAQResponse>>> getActiveFAQs() {
        try {
            List<FAQResponse> faqs = faqService.getActiveFAQs();
            return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, faqs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "FAQs par catégorie", description = "Récupérer les FAQs d'une catégorie")
    public ResponseEntity<ApiResponse<List<FAQResponse>>> getFAQsByCategory(@PathVariable FAQCategory category) {
        try {
            List<FAQResponse> faqs = faqService.getFAQsByCategory(category);
            return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, faqs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Détails d'une FAQ", description = "Récupérer les détails d'une FAQ")
    public ResponseEntity<ApiResponse<FAQResponse>> getFAQ(@PathVariable String id) {
        try {
            FAQResponse faq = faqService.getFAQById(id);
            faqService.incrementViewCount(id);
            return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, faq));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "FAQ non trouvée", null));
        }
    }
}
