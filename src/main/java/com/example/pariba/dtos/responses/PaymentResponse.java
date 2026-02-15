package com.example.pariba.dtos.responses;

import com.example.pariba.enums.PaymentStatus;
import com.example.pariba.enums.PaymentType;
import com.example.pariba.models.Payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public class PaymentResponse {

    private String id;
    private PersonResponse payer;
    private BigDecimal amount;
    private PaymentType paymentType;
    private PaymentStatus status;
    private String externalRef;
    private boolean payout;
    private Instant createdAt;
    private String groupName;
    private String notes;
    private String adminNotes;
    private LocalDateTime validatedAt;

    public PaymentResponse() {}

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.payer = payment.getPayer() != null ? new PersonResponse(payment.getPayer()) : null;
        this.amount = payment.getAmount();
        this.paymentType = payment.getPaymentType();
        this.status = payment.getStatus();
        this.externalRef = payment.getExternalRef();
        this.payout = payment.isPayout();
        this.createdAt = payment.getCreatedAt();
        this.groupName = payment.getGroup() != null ? payment.getGroup().getNom() : null;
        this.notes = payment.getNotes();
        this.adminNotes = payment.getAdminNotes();
        this.validatedAt = payment.getValidatedAt();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public PersonResponse getPayer() { return payer; }
    public void setPayer(PersonResponse payer) { this.payer = payer; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public String getExternalRef() { return externalRef; }
    public void setExternalRef(String externalRef) { this.externalRef = externalRef; }
    public boolean isPayout() { return payout; }
    public void setPayout(boolean payout) { this.payout = payout; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
}
