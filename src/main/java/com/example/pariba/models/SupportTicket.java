package com.example.pariba.models;

import com.example.pariba.enums.TicketPriority;
import com.example.pariba.enums.TicketStatus;
import com.example.pariba.enums.TicketType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Entity
@Table(name = "support_tickets")
public class SupportTicket extends BaseEntity {
    
    @NotBlank(message = "L'ID de la personne est requis")
    @Column(name = "person_id", nullable = false)
    private String personId;
    
    @NotNull(message = "Le type de ticket est requis")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TicketType type;
    
    @NotNull(message = "Le statut est requis")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TicketStatus status = TicketStatus.OPEN;
    
    @NotNull(message = "La priorité est requise")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TicketPriority priority = TicketPriority.MEDIUM;
    
    @NotBlank(message = "Le sujet est requis")
    @Size(min = 5, max = 200, message = "Le sujet doit contenir entre 5 et 200 caractères")
    @Column(nullable = false, length = 200)
    private String subject;
    
    @NotBlank(message = "Le message est requis")
    @Size(min = 10, max = 5000, message = "Le message doit contenir entre 10 et 5000 caractères")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;
    
    @Column(name = "admin_response", columnDefinition = "TEXT")
    private String adminResponse;
    
    @Column(name = "admin_id")
    private String adminId;
    
    @Column(name = "responded_at")
    private Instant respondedAt;
    
    @Column(name = "closed_at")
    private Instant closedAt;
    
    // Constructors
    public SupportTicket() {}
    
    // Getters and Setters
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
}
