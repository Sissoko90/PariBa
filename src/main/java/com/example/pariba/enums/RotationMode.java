package com.example.pariba.enums;

public enum RotationMode { 
    SEQUENTIAL("Séquentiel"), 
    RANDOM("Aléatoire"), 
    SHUFFLE("Mélangé"), 
    CUSTOM("Personnalisé"), 
    FIXED_ORDER("Ordre fixe");
    
    private final String label;
    
    RotationMode(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}