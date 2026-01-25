package com.example.pariba.dtos.requests;

import com.example.pariba.enums.TicketType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateSupportTicketRequest {
    
    @NotNull(message = "Le type de ticket est requis")
    private TicketType type;
    
    @NotBlank(message = "Le sujet est requis")
    @Size(min = 5, max = 200, message = "Le sujet doit contenir entre 5 et 200 caractères")
    private String subject;
    
    @NotBlank(message = "Le message est requis")
    @Size(min = 10, max = 5000, message = "Le message doit contenir entre 10 et 5000 caractères")
    private String message;
    
    // Constructors
    public CreateSupportTicketRequest() {}
    
    // Getters and Setters
    public TicketType getType() {
        return type;
    }
    
    public void setType(TicketType type) {
        this.type = type;
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
}
