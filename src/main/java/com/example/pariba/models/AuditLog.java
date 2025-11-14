package com.example.pariba.models;

import jakarta.persistence.*;

@Entity
@Table(name = "audit_logs", indexes = { @Index(columnList = "actor_person_id"), @Index(columnList = "entityType") })
public class AuditLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_person_id")
    private Person actor;

    @Column(nullable = false)
    private String action; // e.g. CREATE_GROUP, PAY_CONTRIBUTION

    private String entityType; // "TontineGroup","Contribution","Payment"
    private String entityId;

    @Column(length = 4096)
    private String detailsJson;
    
    // Champs pour audit de sécurité
    private String username;  // Pour les événements de sécurité
    private String ipAddress; // Adresse IP de l'utilisateur
    private String eventType; // Type d'événement de sécurité
    private String details;   // Détails de l'événement
    private java.time.Instant timestamp; // Timestamp de l'événement

    public Person getActor() { return actor; }
    public void setActor(Person actor) { this.actor = actor; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getDetailsJson() { return detailsJson; }
    public void setDetailsJson(String detailsJson) { this.detailsJson = detailsJson; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public java.time.Instant getTimestamp() { return timestamp; }
    public void setTimestamp(java.time.Instant timestamp) { this.timestamp = timestamp; }
}