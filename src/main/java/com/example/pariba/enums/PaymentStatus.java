package com.example.pariba.enums;

public enum PaymentStatus { 
    PENDING("En attente"), 
    CONFIRMED("Confirmé"), 
    PROCESSING("En cours de traitement"),
    REJECTED("Rejeté"),
    SUCCESS("Réussi"), 
    FAILED("Échoué");
    
    private final String label;
    
    PaymentStatus(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}