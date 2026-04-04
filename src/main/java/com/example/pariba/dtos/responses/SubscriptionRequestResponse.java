package com.example.pariba.dtos.responses;

import com.example.pariba.enums.SubscriptionRequestStatus;
import com.example.pariba.models.SubscriptionRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO de reponse pour les demandes d'abonnement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequestResponse {
    
    private String id;
    private String personId;
    private String personName;
    private String personPhone;
    private String planId;
    private String planName;
    private String planType;
    private Double planPrice;
    private String billingPeriod;
    private boolean autoRenew;
    private SubscriptionRequestStatus status;
    private String notes;
    private String adminNotes;
    private Instant processedAt;
    private String processedBy;
    private Instant createdAt;
    
    public SubscriptionRequestResponse(SubscriptionRequest request) {
        this.id = request.getId();
        this.personId = request.getPerson().getId();
        this.personName = request.getPerson().getPrenom() + " " + request.getPerson().getNom();
        this.personPhone = request.getPerson().getPhone();
        this.planId = request.getPlan().getId();
        this.planName = request.getPlan().getName();
        this.planType = request.getPlan().getType().name();
        // Calculer le prix selon la période de facturation
        this.planPrice = request.getPlan().getPriceForPeriod(request.getBillingPeriod()) != null ? 
                         request.getPlan().getPriceForPeriod(request.getBillingPeriod()).doubleValue() : 0.0;
        this.billingPeriod = request.getBillingPeriod();
        this.autoRenew = request.isAutoRenew();
        this.status = request.getStatus();
        this.notes = request.getNotes();
        this.adminNotes = request.getAdminNotes();
        this.processedAt = request.getProcessedAt();
        this.processedBy = request.getProcessedBy();
        this.createdAt = request.getCreatedAt();
    }
    
    /**
     * Constructeur avec Person et Plan deja charges (evite lazy loading)
     */
    public SubscriptionRequestResponse(SubscriptionRequest request, 
                                        com.example.pariba.models.Person person, 
                                        com.example.pariba.models.SubscriptionPlan plan) {
        this.id = request.getId();
        this.personId = person.getId();
        this.personName = (person.getPrenom() != null ? person.getPrenom() : "") + " " + 
                          (person.getNom() != null ? person.getNom() : "");
        this.personPhone = person.getPhone();
        this.planId = plan.getId();
        this.planName = plan.getName();
        this.planType = plan.getType() != null ? plan.getType().name() : "";
        // Calculer le prix selon la période de facturation
        this.planPrice = plan.getPriceForPeriod(request.getBillingPeriod()) != null ? 
                         plan.getPriceForPeriod(request.getBillingPeriod()).doubleValue() : 0.0;
        this.billingPeriod = request.getBillingPeriod();
        this.autoRenew = request.isAutoRenew();
        this.status = request.getStatus();
        this.notes = request.getNotes();
        this.adminNotes = request.getAdminNotes();
        this.processedAt = request.getProcessedAt();
        this.processedBy = request.getProcessedBy();
        this.createdAt = request.getCreatedAt();
    }
}
