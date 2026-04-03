package com.example.pariba.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO pour initier une demande d'abonnement
 */
@Data
public class SubscriptionRequestDTO {
    
    @NotBlank(message = "L'ID du plan est requis")
    private String planId;
    
    private String notes; // Notes optionnelles (ex: reference de paiement)
}
