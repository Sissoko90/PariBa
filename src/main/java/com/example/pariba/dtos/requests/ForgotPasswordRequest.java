package com.example.pariba.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    
    @NotBlank(message = "Le numéro de téléphone est requis")
    private String phone;
}
