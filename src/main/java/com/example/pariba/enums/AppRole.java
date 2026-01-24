package com.example.pariba.enums;

public enum AppRole { 
    SUPERADMIN("Super Administrateur"), 
    ADMIN("Administrateur"), 
    USER("Utilisateur");
    
    private final String label;
    
    AppRole(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}
