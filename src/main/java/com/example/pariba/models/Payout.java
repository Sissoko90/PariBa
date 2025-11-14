package com.example.pariba.models;

import com.example.pariba.enums.PaymentStatus;
import com.example.pariba.enums.PaymentType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Entité représentant un paiement sortant (décaissement) vers un bénéficiaire
 */
@Entity
@Table(name = "payouts")
@Data
@EqualsAndHashCode(callSuper = true)
public class Payout extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beneficiary_id", nullable = false)
    private Person beneficiary;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Column(length = 500)
    private String externalRef;
    
    @Column(length = 1000)
    private String notes;
}
