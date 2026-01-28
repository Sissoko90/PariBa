package com.example.pariba.models;

import com.example.pariba.enums.PaymentStatus;
import com.example.pariba.enums.PaymentType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments",
       indexes = { 
           @Index(columnList = "group_id"), 
           @Index(columnList = "payer_person_id"), 
           @Index(columnList = "status"),
           @Index(columnList = "contribution_id")
       })
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private TontineGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_person_id")
    private Person payer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by_person_id")
    private Person validatedBy; // Admin qui a validé le paiement

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    private String externalRef; // Numéro de transaction mobile money
    private String notes; // Notes du payeur
    private String adminNotes; // Notes de l'admin

    private LocalDateTime validatedAt; // Date de validation

    @Column(nullable = false)
    private boolean payout = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contribution_id")
    private Contribution contribution;

    // Getters et setters
    public TontineGroup getGroup() { return group; }
    public void setGroup(TontineGroup group) { this.group = group; }
    public Person getPayer() { return payer; }
    public void setPayer(Person payer) { this.payer = payer; }
    public Person getValidatedBy() { return validatedBy; }
    public void setValidatedBy(Person validatedBy) { this.validatedBy = validatedBy; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public String getExternalRef() { return externalRef; }
    public void setExternalRef(String externalRef) { this.externalRef = externalRef; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
    public boolean isPayout() { return payout; }
    public void setPayout(boolean payout) { this.payout = payout; }
    public Contribution getContribution() { return contribution; }
    public void setContribution(Contribution contribution) { this.contribution = contribution; }
}