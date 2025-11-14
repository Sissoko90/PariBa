package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class UpdateGroupRequest {
    
    @Size(min = 3, max = 100, message = ValidationMessages.SIZE_GROUP_NAME)
    private String nom;
    
    @Size(max = 2048, message = ValidationMessages.SIZE_DESCRIPTION)
    private String description;
    
    @DecimalMin(value = "0.0", message = ValidationMessages.MIN_PENALTY)
    private BigDecimal latePenaltyAmount;
    
    @Min(value = 0, message = ValidationMessages.MIN_GRACE_DAYS)
    @Max(value = 30, message = ValidationMessages.MAX_GRACE_DAYS)
    private Integer graceDays;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getLatePenaltyAmount() { return latePenaltyAmount; }
    public void setLatePenaltyAmount(BigDecimal latePenaltyAmount) { this.latePenaltyAmount = latePenaltyAmount; }
    public Integer getGraceDays() { return graceDays; }
    public void setGraceDays(Integer graceDays) { this.graceDays = graceDays; }
}
