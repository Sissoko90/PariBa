package com.example.pariba.dtos.responses;

import java.math.BigDecimal;

public class DashboardSummaryResponse {
    
    private int totalGroups;
    private int activeGroups;
    private int totalContributions;
    private BigDecimal totalAmountContributed;
    private BigDecimal totalAmountReceived;
    private int upcomingPayments;
    private int unreadNotifications;
    private String nextPaymentDate;
    private BigDecimal nextPaymentAmount;

    public DashboardSummaryResponse() {}

    // Getters and Setters
    public int getTotalGroups() { return totalGroups; }
    public void setTotalGroups(int totalGroups) { this.totalGroups = totalGroups; }
    
    public int getActiveGroups() { return activeGroups; }
    public void setActiveGroups(int activeGroups) { this.activeGroups = activeGroups; }
    
    public int getTotalContributions() { return totalContributions; }
    public void setTotalContributions(int totalContributions) { this.totalContributions = totalContributions; }
    
    public BigDecimal getTotalAmountContributed() { return totalAmountContributed; }
    public void setTotalAmountContributed(BigDecimal totalAmountContributed) { this.totalAmountContributed = totalAmountContributed; }
    
    public BigDecimal getTotalAmountReceived() { return totalAmountReceived; }
    public void setTotalAmountReceived(BigDecimal totalAmountReceived) { this.totalAmountReceived = totalAmountReceived; }
    
    public int getUpcomingPayments() { return upcomingPayments; }
    public void setUpcomingPayments(int upcomingPayments) { this.upcomingPayments = upcomingPayments; }
    
    public int getUnreadNotifications() { return unreadNotifications; }
    public void setUnreadNotifications(int unreadNotifications) { this.unreadNotifications = unreadNotifications; }
    
    public String getNextPaymentDate() { return nextPaymentDate; }
    public void setNextPaymentDate(String nextPaymentDate) { this.nextPaymentDate = nextPaymentDate; }
    
    public BigDecimal getNextPaymentAmount() { return nextPaymentAmount; }
    public void setNextPaymentAmount(BigDecimal nextPaymentAmount) { this.nextPaymentAmount = nextPaymentAmount; }
}
