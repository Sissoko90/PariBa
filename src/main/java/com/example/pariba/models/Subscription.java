package com.example.pariba.models;

import com.example.pariba.enums.SubscriptionStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions", indexes = { @Index(columnList = "person_id"), @Index(columnList = "status") })
public class Subscription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private SubscriptionPlan plan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    private LocalDate startDate;
    private LocalDate endDate;
    
    @Column(nullable = false)
    private boolean autoRenew = false;
    
    @Column(nullable = false, length = 10)
    private String billingPeriod = "monthly"; // monthly, annual
    
    @Column(precision = 19, scale = 2)
    private java.math.BigDecimal pricePaid;

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }
    public SubscriptionPlan getPlan() { return plan; }
    public void setPlan(SubscriptionPlan plan) { this.plan = plan; }
    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public boolean getAutoRenew() { return autoRenew; }
    public void setAutoRenew(boolean autoRenew) { this.autoRenew = autoRenew; }
    
    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }
    
    public java.math.BigDecimal getPricePaid() { return pricePaid; }
    public void setPricePaid(java.math.BigDecimal pricePaid) { this.pricePaid = pricePaid; }
    
    // Méthode pour vérifier si c'est un abonnement annuel
    public boolean isAnnual() { return "annual".equalsIgnoreCase(billingPeriod); }
    
    // Méthode pour calculer la date de fin selon la période
    public LocalDate calculateEndDate(LocalDate startDate) {
        if (isAnnual()) {
            return startDate.plusYears(1);
        } else {
            return startDate.plusMonths(1);
        }
    }
}