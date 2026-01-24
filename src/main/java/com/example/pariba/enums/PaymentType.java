package com.example.pariba.enums;

public enum PaymentType { 
    ORANGE_MONEY("Orange Money"), 
    MOOV_MONEY("Moov Money"), 
    WAVE_MONEY("Wave Money"), 
    CASH("Espèces"), 
    BANK_TRANSFER("Virement bancaire");
    
    private final String label;
    
    PaymentType(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}