package com.example.pariba.dtos.responses;

import java.math.BigDecimal;

public class MemberStatsResponse {
    
    private PersonResponse person;
    private String groupId;
    private Integer totalContributions;
    private Integer paidContributions;
    private Integer pendingContributions;
    private Integer lateContributions;
    private BigDecimal totalPaid;
    private BigDecimal totalDue;
    private BigDecimal totalPenalties;
    private BigDecimal amountReceived; // Si déjà bénéficiaire

    public PersonResponse getPerson() { return person; }
    public void setPerson(PersonResponse person) { this.person = person; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public Integer getTotalContributions() { return totalContributions; }
    public void setTotalContributions(Integer totalContributions) { this.totalContributions = totalContributions; }
    public Integer getPaidContributions() { return paidContributions; }
    public void setPaidContributions(Integer paidContributions) { this.paidContributions = paidContributions; }
    public Integer getPendingContributions() { return pendingContributions; }
    public void setPendingContributions(Integer pendingContributions) { this.pendingContributions = pendingContributions; }
    public Integer getLateContributions() { return lateContributions; }
    public void setLateContributions(Integer lateContributions) { this.lateContributions = lateContributions; }
    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }
    public BigDecimal getTotalDue() { return totalDue; }
    public void setTotalDue(BigDecimal totalDue) { this.totalDue = totalDue; }
    public BigDecimal getTotalPenalties() { return totalPenalties; }
    public void setTotalPenalties(BigDecimal totalPenalties) { this.totalPenalties = totalPenalties; }
    public BigDecimal getAmountReceived() { return amountReceived; }
    public void setAmountReceived(BigDecimal amountReceived) { this.amountReceived = amountReceived; }
}
