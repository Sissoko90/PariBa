package com.example.pariba.enums;

public enum GroupRole { 
    ADMIN("Administrateur"), 
    TREASURER("Trésorier"), 
    MEMBER("Membre");
    
    private final String label;
    
    GroupRole(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}