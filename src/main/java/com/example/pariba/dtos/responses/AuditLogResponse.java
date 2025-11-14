package com.example.pariba.dtos.responses;

import com.example.pariba.models.AuditLog;

import java.time.Instant;

public class AuditLogResponse {
    
    private String id;
    private PersonResponse actor;
    private String action;
    private String entityType;
    private String entityId;
    private String detailsJson;
    private Instant createdAt;

    public AuditLogResponse() {}

    public AuditLogResponse(AuditLog auditLog) {
        this.id = auditLog.getId();
        this.actor = auditLog.getActor() != null ? new PersonResponse(auditLog.getActor()) : null;
        this.action = auditLog.getAction();
        this.entityType = auditLog.getEntityType();
        this.entityId = auditLog.getEntityId();
        this.detailsJson = auditLog.getDetailsJson();
        this.createdAt = auditLog.getCreatedAt();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public PersonResponse getActor() { return actor; }
    public void setActor(PersonResponse actor) { this.actor = actor; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getDetailsJson() { return detailsJson; }
    public void setDetailsJson(String detailsJson) { this.detailsJson = detailsJson; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
