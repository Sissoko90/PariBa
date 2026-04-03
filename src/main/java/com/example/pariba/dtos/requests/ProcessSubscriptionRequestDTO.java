package com.example.pariba.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO pour traiter une demande d'abonnement (admin)
 */
@Data
public class ProcessSubscriptionRequestDTO {
    
    @NotBlank(message = "L'action est requise (APPROVE ou REJECT)")
    private String action; // APPROVE ou REJECT
    
    private String adminNotes; // Notes optionnelles de l'admin
}
