package com.example.pariba.enums;

public enum FAQCategory {
    ACCOUNT("Compte utilisateur"),
    TONTINE("Tontines"),
    PAYMENT("Paiements"),
    SECURITY("Sécurité"),
    FEATURES("Fonctionnalités"),
    TECHNICAL("Technique"),
    GENERAL("Général"),
    OTHER("Autre");
    
    private final String label;
    
    FAQCategory(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}
