package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.SupportContactResponse;
import com.example.pariba.services.ISupportContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/support/contact")
@Tag(name = "Support Contact", description = "Informations de contact du support")
public class SupportContactController {
    
    private final ISupportContactService contactService;
    
    public SupportContactController(ISupportContactService contactService) {
        this.contactService = contactService;
    }
    
    @GetMapping
    @Operation(summary = "Obtenir les informations de contact", description = "Récupère les informations de contact du support")
    public ResponseEntity<ApiResponse<SupportContactResponse>> getContact() {
        try {
            SupportContactResponse contact = contactService.getActiveContact();
            return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, contact));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }
}
