package com.example.pariba.enums;

public enum Frequency { 
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