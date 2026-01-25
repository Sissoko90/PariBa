package com.example.pariba.enums;

public enum AdPlacement { 
    FULLSCREEN("Plein écran"), 
    BANNER("Bannière"), 
    POPUP("Popup");
    
    private final String label;
    
    AdPlacement(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}