package com.example.pariba.enums;

public enum TicketStatus {
    OPEN("Ouvert"),
    IN_PROGRESS("En cours de traitement"),
    WAITING_USER("En attente de réponse utilisateur"),
    RESOLVED("Résolu"),
    CLOSED("Fermé");
    
    private final String label;
    
    TicketStatus(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}
