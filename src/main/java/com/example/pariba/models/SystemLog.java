package com.example.pariba.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Logs système pour tracer toutes les actions importantes
 */
@Entity
@Table(name = "system_logs", indexes = {
    @Index(columnList = "action"),
    @Index(columnList = "userId"),
    @Index(columnList = "createdAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SystemLog extends BaseEntity {
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private String userName;
    
    @Column(nullable = false, length = 100)
    private String action; // LOGIN, LOGOUT, CREATE_GROUP, DELETE_USER, etc.
    
    @Column(length = 100)
    private String entityType; // Person, TontineGroup, Payment, etc.
    
    private String entityId;
    
    @Column(columnDefinition = "TEXT")
    private String details; // JSON ou texte avec détails
    
    private String ipAddress;
    
    private String userAgent;
    
    @Column(nullable = false)
    private String level = "INFO"; // INFO, WARNING, ERROR, CRITICAL
    
    private boolean success = true;
}
