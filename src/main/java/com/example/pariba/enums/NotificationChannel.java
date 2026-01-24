package com.example.pariba.enums;

public enum NotificationChannel { 
    PUSH("Notification Push"), 
    SMS("SMS"), 
    WHATSAPP("WhatsApp"), 
    EMAIL("Email");
    
    private final String label;
    
    NotificationChannel(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}