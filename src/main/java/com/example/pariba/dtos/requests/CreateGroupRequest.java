package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import com.example.pariba.enums.Frequency;
import com.example.pariba.enums.RotationMode;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateGroupRequest {
    
    @NotBlank(message = ValidationMessages.REQUIRED_GROUP_NAME)
    @Size(min = 3, max = 100, message = ValidationMessages.SIZE_GROUP_NAME)
    private String nom;
    
    @Size(max = 2048, message = ValidationMessages.SIZE_DESCRIPTION)
    private String description;
    
    @NotNull(message = ValidationMessages.REQUIRED_AMOUNT)
    @DecimalMin(value = "1000.0", message = ValidationMessages.MIN_AMOUNT)
    private BigDecimal montant;
    
    @NotNull(message = ValidationMessages.REQUIRED_FREQUENCY)
    private Frequency frequency;
    
    @NotNull(message = ValidationMessages.REQUIRED_ROTATION_MODE)
    private RotationMode rotationMode;
    
    @NotNull(message = ValidationMessages.REQUIRED_TOTAL_TOURS)
    @Min(value = 2, message = ValidationMessages.MIN_TOURS)
    @Max(value = 100, message = ValidationMessages.MAX_TOURS)
    private Integer totalTours;
    
    @NotNull(message = ValidationMessages.REQUIRED_START_DATE)
    @Future(message = ValidationMessages.FUTURE_START_DATE)
    private LocalDate startDate;
    
    @DecimalMin(value = "0.0", message = ValidationMessages.MIN_PENALTY)
    private BigDecimal latePenaltyAmount;
    
    @Min(value = 0, message = ValidationMessages.MIN_GRACE_DAYS)
    @Max(value = 30, message = ValidationMessages.MAX_GRACE_DAYS)
    private Integer graceDays;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public Frequency getFrequency() { return frequency; }
    public void setFrequency(Frequency frequency) { this.frequency = frequency; }
    public RotationMode getRotationMode() { return rotationMode; }
    public void setRotationMode(RotationMode rotationMode) { this.rotationMode = rotationMode; }
    public Integer getTotalTours() { return totalTours; }
    public void setTotalTours(Integer totalTours) { this.totalTours = totalTours; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public BigDecimal getLatePenaltyAmount() { return latePenaltyAmount; }
    public void setLatePenaltyAmount(BigDecimal latePenaltyAmount) { this.latePenaltyAmount = latePenaltyAmount; }
    public Integer getGraceDays() { return graceDays; }
    public void setGraceDays(Integer graceDays) { this.graceDays = graceDays; }
}
