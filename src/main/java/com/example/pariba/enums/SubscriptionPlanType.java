package com.example.pariba.enums;

public enum SubscriptionPlanType { 
    FREE("Gratuit"), 
    BASIC("Basique"), 
    PRO("Professionnel"),
    PREMIUM("Premium");
    
    private final String label;
    
    SubscriptionPlanType(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}