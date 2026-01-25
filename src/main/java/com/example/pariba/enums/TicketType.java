package com.example.pariba.enums;

public enum TicketType {
    BUG_REPORT("Signalement de bug"),
    FEATURE_REQUEST("Demande de fonctionnalité"),
    GENERAL_INQUIRY("Question générale"),
    ACCOUNT_ISSUE("Problème de compte"),
    PAYMENT_ISSUE("Problème de paiement"),
    TECHNICAL_ISSUE("Problème technique"),
    OTHER("Autre");
    
    private final String label;
    
    TicketType(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}
