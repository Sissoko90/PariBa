package com.example.pariba.enums;

public enum TourStatus { 
    PENDING("En attente"), 
    SCHEDULED("Planifié"), 
    IN_PROGRESS("En cours"), 
    PAID_OUT("Payé"), 
    COMPLETED("Terminé"), 
    CLOSED("Clôturé");
    
    private final String label;
    
    TourStatus(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}