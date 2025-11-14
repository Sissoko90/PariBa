package com.example.pariba.models;

import com.example.pariba.enums.Frequency;
import com.example.pariba.enums.RotationMode;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "tontine_groups")
public class TontineGroup extends BaseEntity {

    @Column(nullable = false) private String nom;
    @Column(length = 2048) private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal montant; // par tour et par membre

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency = Frequency.MONTHLY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RotationMode rotationMode = RotationMode.FIXED_ORDER;

    @Column(nullable = false) private Integer totalTours;
    private LocalDate startDate;

    @Column(precision = 19, scale = 2) private BigDecimal latePenaltyAmount;
    private Integer graceDays = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_person_id", nullable = false)
    private Person creator;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GroupMembership> memberships = new LinkedHashSet<>();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Tour> tours = new LinkedHashSet<>();

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
    public Person getCreator() { return creator; }
    public void setCreator(Person creator) { this.creator = creator; }
    public Set<GroupMembership> getMemberships() { return memberships; }
    public void setMemberships(Set<GroupMembership> memberships) { this.memberships = memberships; }
    public Set<Tour> getTours() { return tours; }
    public void setTours(Set<Tour> tours) { this.tours = tours; }
}