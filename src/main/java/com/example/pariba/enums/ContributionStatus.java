package com.example.pariba.enums;

public enum ContributionStatus { 
    PENDING("En attente"), 
    DUE("À payer"), 
    PARTIAL("Partiel"), 
    PAID("Payé"), 
    LATE("En retard"), 
    WAIVED("Dispensé");
    
    private final String label;
    
    ContributionStatus(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}