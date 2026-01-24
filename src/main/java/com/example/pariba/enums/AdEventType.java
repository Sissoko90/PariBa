package com.example.pariba.enums;

/**
 * Types d'événements publicitaires
 */
public enum AdEventType {
    IMPRESSION("Impression"),  // L'annonce a été affichée
    CLICK("Clic");             // L'annonce a été cliquée
    
    private final String label;
    
    AdEventType(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}
