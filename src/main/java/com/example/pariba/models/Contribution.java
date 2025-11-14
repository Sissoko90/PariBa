package com.example.pariba.models;

import com.example.pariba.enums.ContributionStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contributions",
       indexes = { @Index(columnList = "group_id"), @Index(columnList = "member_person_id"), @Index(columnList = "tour_id") })
public class Contribution extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private TontineGroup group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_person_id", nullable = false)
    private Person member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amountDue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContributionStatus status = ContributionStatus.DUE;

    private LocalDate dueDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", unique = true)
    private Payment payment;

    @Column(precision = 19, scale = 2)
    private BigDecimal penaltyApplied;

    public TontineGroup getGroup() { return group; }
    public void setGroup(TontineGroup group) { this.group = group; }
    public Person getMember() { return member; }
    public void setMember(Person member) { this.member = member; }
    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }
    public BigDecimal getAmountDue() { return amountDue; }
    public void setAmountDue(BigDecimal amountDue) { this.amountDue = amountDue; }
    public ContributionStatus getStatus() { return status; }
    public void setStatus(ContributionStatus status) { this.status = status; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    public BigDecimal getPenaltyApplied() { return penaltyApplied; }
    public void setPenaltyApplied(BigDecimal penaltyApplied) { this.penaltyApplied = penaltyApplied; }
}