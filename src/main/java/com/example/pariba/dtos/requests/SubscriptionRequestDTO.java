package com.example.pariba.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO pour initier une demande d'abonnement
 */
@Data
public class SubscriptionRequestDTO {
    
    @NotBlank(message = "L'ID du plan est requis")
    private String planId;
    
    @Pattern(regexp = "^(monthly|annual)$", message = "La période doit être 'monthly' ou 'annual'")
    private String billingPeriod = "monthly"; // monthly, annual
    
    private String notes; // Notes optionnelles (ex: reference de paiement)
}
