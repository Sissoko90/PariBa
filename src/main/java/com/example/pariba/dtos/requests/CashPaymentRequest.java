package com.example.pariba.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashPaymentRequest {
    
    @NotBlank(message = "L'ID du membre est requis")
    private String memberId;
    
    @NotBlank(message = "L'ID de la contribution est requis")
    private String contributionId;
    
    @NotNull(message = "Le montant est requis")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal amount;
    
    private String notes;
}
