package com.example.pariba.dtos.responses;

import com.example.pariba.enums.SubscriptionPlanType;
import com.example.pariba.models.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanResponse {
    
    private String id;
    private SubscriptionPlanType type;
    private String name;
    private String description;
    private BigDecimal monthlyPrice;
    private BigDecimal annualPrice;
    private String features;
    private String featuresJson;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    
    // Limites et fonctionnalités du plan
    private Integer maxGroups;
    private Boolean canExportPdf;
    private Boolean canExportExcel;
    
    public SubscriptionPlanResponse(SubscriptionPlan plan) {
        this.id = plan.getId();
        this.type = plan.getType();
        this.name = plan.getName();
        this.description = plan.getDescription();
        this.monthlyPrice = plan.getMonthlyPrice();
        this.annualPrice = plan.getAnnualPrice();
        this.features = plan.getFeatures();
        this.featuresJson = plan.getFeaturesJson();
        this.active = plan.getActive();
        this.createdAt = plan.getCreatedAt();
        this.updatedAt = plan.getUpdatedAt();
        // Limites et fonctionnalités
        this.maxGroups = plan.getMaxGroups();
        this.canExportPdf = plan.getCanExportPdf();
        this.canExportExcel = plan.getCanExportExcel();
    }
}
