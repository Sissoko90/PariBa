package com.example.pariba.dtos.responses;

import com.example.pariba.enums.ContributionStatus;
import com.example.pariba.models.Contribution;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class ContributionResponse {
    
    private String id;
    private PersonResponse member;
    private String tourId;
    private Integer tourIndex;
    private BigDecimal amountDue;
    private ContributionStatus status;
    private LocalDate dueDate;
    private BigDecimal penaltyApplied;
    private Instant createdAt;

    public ContributionResponse() {}

    public ContributionResponse(Contribution contribution) {
        this.id = contribution.getId();
        this.member = new PersonResponse(contribution.getMember());
        this.tourId = contribution.getTour().getId();
        this.tourIndex = contribution.getTour().getIndexInGroup();
        this.amountDue = contribution.getAmountDue();
        this.status = contribution.getStatus();
        this.dueDate = contribution.getDueDate();
        this.penaltyApplied = contribution.getPenaltyApplied();
        this.createdAt = contribution.getCreatedAt();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public PersonResponse getMember() { return member; }
    public void setMember(PersonResponse member) { this.member = member; }
    public String getTourId() { return tourId; }
    public void setTourId(String tourId) { this.tourId = tourId; }
    public Integer getTourIndex() { return tourIndex; }
    public void setTourIndex(Integer tourIndex) { this.tourIndex = tourIndex; }
    public BigDecimal getAmountDue() { return amountDue; }
    public void setAmountDue(BigDecimal amountDue) { this.amountDue = amountDue; }
    public ContributionStatus getStatus() { return status; }
    public void setStatus(ContributionStatus status) { this.status = status; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public BigDecimal getPenaltyApplied() { return penaltyApplied; }
    public void setPenaltyApplied(BigDecimal penaltyApplied) { this.penaltyApplied = penaltyApplied; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
