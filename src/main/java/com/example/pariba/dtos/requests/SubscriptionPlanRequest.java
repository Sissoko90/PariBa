package com.example.pariba.dtos.requests;

import com.example.pariba.enums.SubscriptionPlanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubscriptionPlanRequest {
    
    @NotNull(message = "Le type de plan est requis")
    private SubscriptionPlanType type;
    
    @NotBlank(message = "Le nom est requis")
    private String name;
    
    private String description;
    
    @NotNull(message = "Le prix mensuel est requis")
    @PositiveOrZero(message = "Le prix doit être positif ou zéro")
    private BigDecimal monthlyPrice;
    
    private String features;
    
    private String featuresJson;
    
    private Boolean active = true;
}
