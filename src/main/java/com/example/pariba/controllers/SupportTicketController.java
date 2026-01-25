package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.CreateSupportTicketRequest;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.SupportTicketResponse;
import com.example.pariba.services.ISupportTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support/tickets")
@Tag(name = "Support Tickets", description = "Gestion des tickets de support")
@SecurityRequirement(name = "bearerAuth")
public class SupportTicketController {
    
    private final ISupportTicketService supportTicketService;
    
    public SupportTicketController(ISupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }
    
    private String getPersonIdFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Créer un ticket de support", description = "Créer un nouveau ticket de support")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> createTicket(
            @Valid @RequestBody CreateSupportTicketRequest request) {
        try {
            String personId = getPersonIdFromAuth();
            if (personId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }
            
            SupportTicketResponse ticket = supportTicketService.createTicket(request, personId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Ticket créé avec succès", ticket));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Mes tickets", description = "Récupérer tous mes tickets de support")
    public ResponseEntity<ApiResponse<List<SupportTicketResponse>>> getMyTickets() {
        try {
            String personId = getPersonIdFromAuth();
            if (personId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non authentifié", null));
            }
            
            List<SupportTicketResponse> tickets = supportTicketService.getTicketsByPerson(personId);
            return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, tickets));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Détails d'un ticket", description = "Récupérer les détails d'un ticket")
    public ResponseEntity<ApiResponse<SupportTicketResponse>> getTicket(@PathVariable String id) {
        try {
            SupportTicketResponse ticket = supportTicketService.getTicketById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, ticket));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Ticket non trouvé", null));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Supprimer un ticket", description = "Supprimer un ticket de support")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(@PathVariable String id) {
        try {
            supportTicketService.deleteTicket(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Ticket supprimé avec succès", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur: " + e.getMessage(), null));
        }
    }
}
