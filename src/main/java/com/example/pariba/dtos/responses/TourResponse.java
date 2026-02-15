package com.example.pariba.dtos.responses;

import com.example.pariba.enums.TourStatus;
import com.example.pariba.models.Tour;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class TourResponse {
    
    private String id;
    private Integer indexInGroup;
    private PersonResponse beneficiary;
    private LocalDate scheduledDate;
    private TourStatus status;
    private BigDecimal totalDue;
    private BigDecimal totalCollected;
    private Instant createdAt;

    public TourResponse() {}

    public TourResponse(Tour tour) {
        this.id = tour.getId();
        this.indexInGroup = tour.getIndexInGroup();
        this.beneficiary = new PersonResponse(tour.getBeneficiary());
        this.scheduledDate = tour.getScheduledDate();
        this.status = tour.getStatus();
        // Utiliser expectedAmount si totalDue est null (pour les tours PENDING)
        this.totalDue = tour.getTotalDue() != null ? tour.getTotalDue() : tour.getExpectedAmount();
        this.totalCollected = tour.getTotalCollected();
        this.createdAt = tour.getCreatedAt();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Integer getIndexInGroup() { return indexInGroup; }
    public void setIndexInGroup(Integer indexInGroup) { this.indexInGroup = indexInGroup; }
    public PersonResponse getBeneficiary() { return beneficiary; }
    public void setBeneficiary(PersonResponse beneficiary) { this.beneficiary = beneficiary; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
    public TourStatus getStatus() { return status; }
    public void setStatus(TourStatus status) { this.status = status; }
    public BigDecimal getTotalDue() { return totalDue; }
    public void setTotalDue(BigDecimal totalDue) { this.totalDue = totalDue; }
    public BigDecimal getTotalCollected() { return totalCollected; }
    public void setTotalCollected(BigDecimal totalCollected) { this.totalCollected = totalCollected; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
