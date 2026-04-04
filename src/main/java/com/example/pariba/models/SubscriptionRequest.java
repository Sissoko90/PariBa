package com.example.pariba.models;

import com.example.pariba.enums.SubscriptionRequestStatus;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * Demande d'abonnement initiee par un utilisateur
 * Doit etre validee par un admin avant activation
 */
@Entity
@Table(name = "subscription_requests")
public class SubscriptionRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionRequestStatus status = SubscriptionRequestStatus.PENDING;

    @Column(length = 500)
    private String notes; // Notes de l'utilisateur (ex: reference de paiement)
    
    @Column(nullable = false, length = 10)
    private String billingPeriod = "monthly"; // monthly, annual
    
    @Column(nullable = false)
    private boolean autoRenew = false;

    @Column(length = 500)
    private String adminNotes; // Notes de l'admin (ex: raison du rejet)

    @Column(name = "processed_at")
    private Instant processedAt; // Date de traitement par l'admin

    @Column(name = "processed_by")
    private String processedBy; // ID de l'admin qui a traite

    // Getters et Setters
    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public SubscriptionPlan getPlan() { return plan; }
    public void setPlan(SubscriptionPlan plan) { this.plan = plan; }

    public SubscriptionRequestStatus getStatus() { return status; }
    public void setStatus(SubscriptionRequestStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public Instant getProcessedAt() { return processedAt; }
    public void setProcessedAt(Instant processedAt) { this.processedAt = processedAt; }

    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }
    
    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }
    
    public boolean isAutoRenew() { return autoRenew; }
    public void setAutoRenew(boolean autoRenew) { this.autoRenew = autoRenew; }
}
