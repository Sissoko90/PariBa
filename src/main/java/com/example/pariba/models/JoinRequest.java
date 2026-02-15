package com.example.pariba.models;

import com.example.pariba.enums.JoinRequestStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "join_requests", indexes = {
    @Index(columnList = "group_id"),
    @Index(columnList = "person_id"),
    @Index(columnList = "status"),
    @Index(columnList = "createdAt")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"group_id", "person_id"})
})
public class JoinRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private TontineGroup group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JoinRequestStatus status = JoinRequestStatus.PENDING;

    @Column(length = 500)
    private String message; // Message optionnel de la personne qui demande

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private Person reviewedBy; // Admin qui a approuvé/rejeté

    private Instant reviewedAt;

    @Column(length = 500)
    private String reviewNote; // Note de l'admin lors de l'approbation/rejet

    // Getters et Setters
    public TontineGroup getGroup() { return group; }
    public void setGroup(TontineGroup group) { this.group = group; }

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public JoinRequestStatus getStatus() { return status; }
    public void setStatus(JoinRequestStatus status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Person getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(Person reviewedBy) { this.reviewedBy = reviewedBy; }

    public Instant getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Instant reviewedAt) { this.reviewedAt = reviewedAt; }

    public String getReviewNote() { return reviewNote; }
    public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }
}
