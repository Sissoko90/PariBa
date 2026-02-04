package com.example.pariba.dtos.responses;

import com.example.pariba.enums.PaymentStatus;
import com.example.pariba.enums.PaymentType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class PaymentHistoryResponse {

    private String id;
    private String tourNumber; // "Tour #1", "Tour #2", etc.
    private String tourTitle;  // Titre du tour si disponible
    private BigDecimal amount;
    private PaymentType paymentType;
    private PaymentStatus status;
    private LocalDate paymentDate;
    private String formattedDate; // Formaté pour l'affichage, ex: "15 Nov 2025"
    private boolean isPayout;

    // Constructeurs
    public PaymentHistoryResponse() {}

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTourNumber() { return tourNumber; }
    public void setTourNumber(String tourNumber) { this.tourNumber = tourNumber; }
    
    public String getTourTitle() { return tourTitle; }
    public void setTourTitle(String tourTitle) { this.tourTitle = tourTitle; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
    
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    
    public String getFormattedDate() { return formattedDate; }
    public void setFormattedDate(String formattedDate) { this.formattedDate = formattedDate; }
    
    public boolean isPayout() { return isPayout; }
    public void setPayout(boolean payout) { isPayout = payout; }
}