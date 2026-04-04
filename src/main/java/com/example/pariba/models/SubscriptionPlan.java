package com.example.pariba.models;

import com.example.pariba.enums.SubscriptionPlanType;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionPlanType type = SubscriptionPlanType.FREE;

    @Column(nullable = false)
    private String name; // Free, Basic, Pro

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyPrice = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal annualPrice;

    @Column(length = 2000)
    private String featuresJson;

    @Column(nullable = false)
    private Boolean active = true;
    
    // Limites du plan
    @Column(nullable = false)
    private Integer maxGroups = 2; // Nombre max de tontines (0 = illimité)
    
    @Column(nullable = false)
    private Boolean canExportPdf = false; // Export PDF
    
    @Column(nullable = false)
    private Boolean canExportExcel = false; // Export Excel
    

    public SubscriptionPlanType getType() { return type; }
    public void setType(SubscriptionPlanType type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(BigDecimal monthlyPrice) { this.monthlyPrice = monthlyPrice; }
    public String getFeaturesJson() { return featuresJson; }
    public void setFeaturesJson(String featuresJson) { this.featuresJson = featuresJson; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    // Getters/Setters pour les limites
    public Integer getMaxGroups() { return maxGroups; }
    public void setMaxGroups(Integer maxGroups) { this.maxGroups = maxGroups; }
    public Boolean getCanExportPdf() { return canExportPdf; }
    public void setCanExportPdf(Boolean canExportPdf) { this.canExportPdf = canExportPdf; }
    public Boolean getCanExportExcel() { return canExportExcel; }
    public void setCanExportExcel(Boolean canExportExcel) { this.canExportExcel = canExportExcel; }
    // Méthode utilitaire pour vérifier si les groupes sont illimités
    public boolean hasUnlimitedGroups() { return maxGroups == null || maxGroups == 0; }
    
    // Alias pour compatibilité
    public BigDecimal getPrice() { return monthlyPrice; }
    public String getFeatures() { return featuresJson; }
    public void setFeatures(String features) { this.featuresJson = features; }
    
    // Getters/Setters pour annualPrice
    public BigDecimal getAnnualPrice() { return annualPrice; }
    public void setAnnualPrice(BigDecimal annualPrice) { this.annualPrice = annualPrice; }
    
    // Méthode pour vérifier si le plan a un prix annuel
    public boolean hasAnnualPrice() { return annualPrice != null && annualPrice.compareTo(BigDecimal.ZERO) > 0; }
    
    // Méthode pour obtenir le prix selon la période
    public BigDecimal getPriceForPeriod(String period) {
        if ("annual".equalsIgnoreCase(period)) {
            // Si un prix annuel est défini, l'utiliser, sinon calculer (prix mensuel x 12)
            if (hasAnnualPrice()) {
                return annualPrice;
            } else {
                return monthlyPrice.multiply(BigDecimal.valueOf(12));
            }
        }
        return monthlyPrice;
    }
}