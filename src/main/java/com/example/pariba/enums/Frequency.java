package com.example.pariba.enums;

public enum Frequency { 
    DAILY("Quotidien"),
    WEEKLY("Hebdomadaire"), 
    BIWEEKLY("Bi-hebdomadaire"), 
    MONTHLY("Mensuel"),
    QUARTERLY("Trimestriel"),
    YEARLY("Annuel"),
    
    // Anciens noms français (deprecated mais gardés pour compatibilité)
    HEBDOMADAIRE("Hebdomadaire"), 
    BIHEBDOMADAIRE("Bi-hebdomadaire"), 
    MENSUEL("Mensuel");
    
    private final String label;
    
    Frequency(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}