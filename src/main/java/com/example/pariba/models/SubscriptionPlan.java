package com.example.pariba.models;

import com.example.pariba.enums.SubscriptionPlanType;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPlanType type = SubscriptionPlanType.FREE;

    @Column(nullable = false)
    private String name; // Free, Basic, Pro

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyPrice = BigDecimal.ZERO;

    private String featuresJson;

    public SubscriptionPlanType getType() { return type; }
    public void setType(SubscriptionPlanType type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getMonthlyPrice() { return monthlyPrice; }
    public void setMonthlyPrice(BigDecimal monthlyPrice) { this.monthlyPrice = monthlyPrice; }
    public String getFeaturesJson() { return featuresJson; }
    public void setFeaturesJson(String featuresJson) { this.featuresJson = featuresJson; }
    
    // Alias pour compatibilité
    public BigDecimal getPrice() { return monthlyPrice; }
    public String getFeatures() { return featuresJson; }
}