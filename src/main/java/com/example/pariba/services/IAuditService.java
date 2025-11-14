package com.example.pariba.services;

import com.example.pariba.dtos.responses.AuditLogResponse;

import java.time.Instant;
import java.util.List;

public interface IAuditService {
    void log(String actorId, String action, String entityType, String entityId, String detailsJson);
    void log(String actorId, String action, String entityType, String entityId, String detailsJson, String ipAddress);
    List<AuditLogResponse> getLogsByEntity(String entityType, String entityId);
    List<AuditLogResponse> getLogsByActor(String actorId);
    List<AuditLogResponse> getLogsByDateRange(Instant start, Instant end);
}
