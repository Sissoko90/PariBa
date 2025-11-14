package com.example.pariba.dtos.responses;

import com.example.pariba.enums.SubscriptionPlanType;
import com.example.pariba.enums.SubscriptionStatus;
import com.example.pariba.models.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    
    private String id;
    private SubscriptionPlanType planType;
    private String planName;
    private BigDecimal monthlyPrice;
    private SubscriptionStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String featuresJson;
    private Instant createdAt;
    private boolean autoRenew;

    /**
     * Constructeur personnalisé à partir d'une entité Subscription
     */
    public SubscriptionResponse(Subscription subscription) {
        this.id = subscription.getId();
        if (subscription.getPlan() != null) {
            this.planType = subscription.getPlan().getType();
            this.planName = subscription.getPlan().getName();
            this.monthlyPrice = subscription.getPlan().getMonthlyPrice();
            this.featuresJson = subscription.getPlan().getFeaturesJson();
        }
        this.status = subscription.getStatus();
        this.startDate = subscription.getStartDate();
        this.endDate = subscription.getEndDate();
        this.autoRenew = subscription.getAutoRenew();
        this.createdAt = subscription.getCreatedAt();
    }
}
