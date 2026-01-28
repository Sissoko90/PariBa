// DeclarePaymentRequest.java
package com.example.pariba.dtos.requests;

import com.example.pariba.enums.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DeclarePaymentRequest {
    @NotBlank(message = "L'ID de la contribution est obligatoire")
    private String contributionId;
    
    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal amount;
    
    @NotNull(message = "Le type de paiement est obligatoire")
    private PaymentType paymentType;
    
    private String transactionRef; // Numéro de transaction mobile money
    
    private String notes; // Notes supplémentaires
}