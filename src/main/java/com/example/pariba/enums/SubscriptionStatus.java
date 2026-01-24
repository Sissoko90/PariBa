package com.example.pariba.enums;

public enum SubscriptionStatus { 
    ACTIVE("Actif"), 
    EXPIRED("Expiré"), 
    CANCELED("Annulé"),
    CANCELLED("Annulé");  // Alias britannique
    
    private final String label;
    
    SubscriptionStatus(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}