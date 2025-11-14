package com.example.pariba.dtos.responses;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {
    
    private GroupResponse group;
    private Integer totalMembers;
    private Integer totalTours;
    private Integer completedTours;
    private Integer pendingTours;
    private BigDecimal totalCollected;
    private BigDecimal totalDue;
    private BigDecimal totalPaidOut;
    private TourResponse currentTour;
    private List<ContributionResponse> pendingContributions;
    private List<PaymentResponse> recentPayments;

    public GroupResponse getGroup() { return group; }
    public void setGroup(GroupResponse group) { this.group = group; }
    public Integer getTotalMembers() { return totalMembers; }
    public void setTotalMembers(Integer totalMembers) { this.totalMembers = totalMembers; }
    public Integer getTotalTours() { return totalTours; }
    public void setTotalTours(Integer totalTours) { this.totalTours = totalTours; }
    public Integer getCompletedTours() { return completedTours; }
    public void setCompletedTours(Integer completedTours) { this.completedTours = completedTours; }
    public Integer getPendingTours() { return pendingTours; }
    public void setPendingTours(Integer pendingTours) { this.pendingTours = pendingTours; }
    public BigDecimal getTotalCollected() { return totalCollected; }
    public void setTotalCollected(BigDecimal totalCollected) { this.totalCollected = totalCollected; }
    public BigDecimal getTotalDue() { return totalDue; }
    public void setTotalDue(BigDecimal totalDue) { this.totalDue = totalDue; }
    public BigDecimal getTotalPaidOut() { return totalPaidOut; }
    public void setTotalPaidOut(BigDecimal totalPaidOut) { this.totalPaidOut = totalPaidOut; }
    public TourResponse getCurrentTour() { return currentTour; }
    public void setCurrentTour(TourResponse currentTour) { this.currentTour = currentTour; }
    public List<ContributionResponse> getPendingContributions() { return pendingContributions; }
    public void setPendingContributions(List<ContributionResponse> pendingContributions) { this.pendingContributions = pendingContributions; }
    public List<PaymentResponse> getRecentPayments() { return recentPayments; }
    public void setRecentPayments(List<PaymentResponse> recentPayments) { this.recentPayments = recentPayments; }
}
