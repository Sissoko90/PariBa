package com.example.pariba.models;

import com.example.pariba.enums.TourStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tours",
       indexes = { @Index(columnList = "group_id"), @Index(columnList = "indexInGroup") })
public class Tour extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private TontineGroup group;

    @Column(nullable = false)
    private Integer indexInGroup; // 1..N

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "beneficiary_person_id", nullable = false)
    private Person beneficiary;

    private LocalDate scheduledDate;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourStatus status = TourStatus.SCHEDULED;

    @Column(precision = 19, scale = 2) private BigDecimal totalDue;
    @Column(precision = 19, scale = 2) private BigDecimal totalCollected;
    @Column(precision = 19, scale = 2) private BigDecimal expectedAmount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payout_payment_id", unique = true)
    private Payment payoutPayment;

    public TontineGroup getGroup() { return group; }
    public void setGroup(TontineGroup group) { this.group = group; }
    public Integer getIndexInGroup() { return indexInGroup; }
    public void setIndexInGroup(Integer indexInGroup) { this.indexInGroup = indexInGroup; }
    public Person getBeneficiary() { return beneficiary; }
    public void setBeneficiary(Person beneficiary) { this.beneficiary = beneficiary; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public TourStatus getStatus() { return status; }
    public void setStatus(TourStatus status) { this.status = status; }
    public BigDecimal getTotalDue() { return totalDue; }
    public void setTotalDue(BigDecimal totalDue) { this.totalDue = totalDue; }
    public BigDecimal getTotalCollected() { return totalCollected; }
    public void setTotalCollected(BigDecimal totalCollected) { this.totalCollected = totalCollected; }
    public BigDecimal getExpectedAmount() { return expectedAmount; }
    public void setExpectedAmount(BigDecimal expectedAmount) { this.expectedAmount = expectedAmount; }
    public Payment getPayoutPayment() { return payoutPayment; }
    public void setPayoutPayment(Payment payoutPayment) { this.payoutPayment = payoutPayment; }
}