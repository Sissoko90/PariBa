package com.example.pariba.enums;

public enum TicketPriority {
    LOW("Basse"),
    MEDIUM("Moyenne"),
    HIGH("Haute"),
    URGENT("Urgente");
    
    private final String label;
    
    TicketPriority(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}
