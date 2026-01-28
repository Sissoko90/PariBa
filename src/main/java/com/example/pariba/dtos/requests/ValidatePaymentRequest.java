// ValidatePaymentRequest.java
package com.example.pariba.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ValidatePaymentRequest {
    @NotBlank(message = "L'ID du paiement est obligatoire")
    private String paymentId;
    
    @NotNull(message = "Le statut de confirmation est obligatoire")
    private boolean confirmed;
    
    private String notes; // Raison de rejet ou notes
}