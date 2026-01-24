package com.example.pariba.enums;

public enum DelegationStatus { 
    PENDING("En attente"), 
    APPROVED("Approuvé"), 
    REVOKED("Révoqué");
    
    private final String label;
    
    DelegationStatus(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}