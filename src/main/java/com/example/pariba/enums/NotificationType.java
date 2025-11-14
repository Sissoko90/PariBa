package com.example.pariba.enums;

public enum NotificationType { 
    // Notifications générales
    WELCOME,
    REMINDER_DUE,
    INVITE,
    
    // OTP et sécurité
    OTP_VERIFICATION,
    
    // Notifications de paiement
    PAYMENT_SUCCESS,
    PAYMENT_FAILED,
    CONTRIBUTION_RECEIVED,
    PAYOUT_PROCESSED,
    
    // Notifications de groupe
    GROUP_INVITATION,
    GROUP_JOINED,
    GROUP_LEFT,
    
    // Rappels
    CONTRIBUTION_REMINDER,
    TOUR_REMINDER,
    
    // Exports et rapports
    EXPORT_READY
}