package com.example.pariba.models;

import com.example.pariba.enums.PaymentStatus;
import com.example.pariba.enums.PaymentType;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payments",
       indexes = { @Index(columnList = "group_id"), @Index(columnList = "payer_person_id"), @Index(columnList = "status") })
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private TontineGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_person_id")
    private Person payer; // cotisant (ou bénéficiaire selon payout)

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    private String externalRef; // ID opérateur mobile money
    private String invoice;     // URL/base64 reçu

    @Column(nullable = false)
    private boolean payout = false; // déboursement au bénéficiaire

    @OneToOne(mappedBy = "payment", fetch = FetchType.LAZY)
    private Contribution contribution;

    public TontineGroup getGroup() { return group; }
    public void setGroup(TontineGroup group) { this.group = group; }
    public Person getPayer() { return payer; }
    public void setPayer(Person payer) { this.payer = payer; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public String getExternalRef() { return externalRef; }
    public void setExternalRef(String externalRef) { this.externalRef = externalRef; }
    public String getInvoice() { return invoice; }
    public void setInvoice(String invoice) { this.invoice = invoice; }
    public boolean isPayout() { return payout; }
    public void setPayout(boolean payout) { this.payout = payout; }
    public Contribution getContribution() { return contribution; }
    public void setContribution(Contribution contribution) { this.contribution = contribution; }
}