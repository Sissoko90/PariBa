package com.example.pariba.dtos.responses;

import com.example.pariba.enums.Frequency;
import com.example.pariba.enums.GroupRole;
import com.example.pariba.enums.RotationMode;
import com.example.pariba.models.TontineGroup;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class GroupResponse {
    
    private String id;
    private String nom;
    private String description;
    private BigDecimal montant;
    private Frequency frequency;
    private RotationMode rotationMode;
    private Integer totalTours;
    private LocalDate startDate;
    private BigDecimal latePenaltyAmount;
    private Integer graceDays;
    private PersonResponse creator;
    private Integer memberCount;
    private GroupRole currentUserRole;
    private Instant createdAt;

    public GroupResponse() {}

    public GroupResponse(TontineGroup group) {
        this.id = group.getId();
        this.nom = group.getNom();
        this.description = group.getDescription();
        this.montant = group.getMontant();
        this.frequency = group.getFrequency();
        this.rotationMode = group.getRotationMode();
        this.totalTours = group.getTotalTours();
        this.startDate = group.getStartDate();
        this.latePenaltyAmount = group.getLatePenaltyAmount();
        this.graceDays = group.getGraceDays();
        this.creator = new PersonResponse(group.getCreator());
        this.memberCount = group.getMemberships() != null ? group.getMemberships().size() : 0;
        this.createdAt = group.getCreatedAt();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public Frequency getFrequency() { return frequency; }
    public void setFrequency(Frequency frequency) { this.frequency = frequency; }
    public RotationMode getRotationMode() { return rotationMode; }
    public void setRotationMode(RotationMode rotationMode) { this.rotationMode = rotationMode; }
    public Integer getTotalTours() { return totalTours; }
    public void setTotalTours(Integer totalTours) { this.totalTours = totalTours; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public BigDecimal getLatePenaltyAmount() { return latePenaltyAmount; }
    public void setLatePenaltyAmount(BigDecimal latePenaltyAmount) { this.latePenaltyAmount = latePenaltyAmount; }
    public Integer getGraceDays() { return graceDays; }
    public void setGraceDays(Integer graceDays) { this.graceDays = graceDays; }
    public PersonResponse getCreator() { return creator; }
    public void setCreator(PersonResponse creator) { this.creator = creator; }
    public Integer getMemberCount() { return memberCount; }
    public void setMemberCount(Integer memberCount) { this.memberCount = memberCount; }
    public GroupRole getCurrentUserRole() { return currentUserRole; }
    public void setCurrentUserRole(GroupRole currentUserRole) { this.currentUserRole = currentUserRole; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
