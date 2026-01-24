package com.example.pariba.enums;

public enum Frequency { 
    WEEKLY("Hebdomadaire"), 
    BIWEEKLY("Bi-hebdomadaire"), 
    MONTHLY("Mensuel");
    
    private final String label;
    
    Frequency(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}