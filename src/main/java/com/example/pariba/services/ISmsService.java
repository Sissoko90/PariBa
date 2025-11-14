package com.example.pariba.services;

/**
 * Service pour l'envoi de SMS et WhatsApp via Twilio
 */
public interface ISmsService {
    
    /**
     * Envoyer un SMS simple
     */
    void sendSms(String phoneNumber, String message);
    
    /**
     * Envoyer un code OTP par SMS
     */
    void sendOtpSms(String phoneNumber, String otpCode);
    
    /**
     * Envoyer un message WhatsApp
     */
    void sendWhatsApp(String phoneNumber, String message);
    
    /**
     * Envoyer un code OTP par WhatsApp
     */
    void sendOtpWhatsApp(String phoneNumber, String otpCode);
}
