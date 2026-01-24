package com.example.pariba.enums;

public enum InvitationStatus { 
    PENDING("En attente"), 
    ACCEPTED("Acceptée"), 
    DECLINED("Refusée"), 
    EXPIRED("Expirée");
    
    private final String label;
    
    InvitationStatus(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}