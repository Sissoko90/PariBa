package com.example.pariba.enums;

public enum DocumentType { 
    RECEIPT("Reçu"), 
    EXPORT_REPORT("Rapport d'export"), 
    ID_CARD("Carte d'identité"), 
    GROUP_RULES("Règlement du groupe");
    
    private final String label;
    
    DocumentType(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}