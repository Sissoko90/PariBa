package com.example.pariba.dtos.responses;

import com.example.pariba.enums.TicketPriority;
import com.example.pariba.enums.TicketStatus;
import com.example.pariba.enums.TicketType;

import java.time.Instant;

public class SupportTicketResponse {
    
    private String id;
    private String personId;
    private TicketType type;
    private TicketStatus status;
    private TicketPriority priority;
    private String subject;
    private String message;
    private String adminResponse;
    private String adminId;
    private Instant respondedAt;
    private Instant closedAt;
    private Instant createdAt;
    private Instant updatedAt;
    
    // Constructors
    public SupportTicketResponse() {}
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPersonId() {
        return personId;
    }
    
    public void setPersonId(String personId) {
        this.personId = personId;
    }
    
    public TicketType getType() {
        return type;
    }
    
    public void setType(TicketType type) {
        this.type = type;
    }
    
    public TicketStatus getStatus() {
        return status;
    }
    
    public void setStatus(TicketStatus status) {
        this.status = status;
    }
    
    public TicketPriority getPriority() {
        return priority;
    }
    
    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getAdminResponse() {
        return adminResponse;
    }
    
    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }
    
    public String getAdminId() {
        return adminId;
    }
    
    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
    
    public Instant getRespondedAt() {
        return respondedAt;
    }
    
    public void setRespondedAt(Instant respondedAt) {
        this.respondedAt = respondedAt;
    }
    
    public Instant getClosedAt() {
        return closedAt;
    }
    
    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
