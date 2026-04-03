package com.example.pariba.services.impl;

import com.example.pariba.services.ISmsService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service SMS avec Twilio
 */
@Service
@Slf4j
public class SmsServiceImpl implements ISmsService {
    
    @Value("${SMS_ACCOUNT_SID:${sms.account-sid:}}")
    private String accountSid;
    
    @Value("${SMS_AUTH_TOKEN:${sms.auth-token:}}")
    private String authToken;
    
    @Value("${SMS_SENDER_ID:${sms.sender-id:}}")
    private String senderPhoneNumber;
    
    @Value("${SMS_ENABLED:${sms.enabled:false}}")
    private boolean smsEnabled;
    
    private boolean twilioInitialized = false;
    
    /**
     * Initialiser Twilio
     */
    private void initTwilio() {
        if (!twilioInitialized && smsEnabled && accountSid != null && !accountSid.isEmpty()) {
            log.info("Initialisation Twilio avec SID: {}...", accountSid.substring(0, Math.min(10, accountSid.length())));
            Twilio.init(accountSid, authToken);
            twilioInitialized = true;
            log.info("Twilio initialise avec succes");
        }
    }
    
    @Override
    public void sendSms(String phoneNumber, String message) {
        if (!smsEnabled) {
            log.warn("SMS n'est pas activé. SMS non envoyé à: {}", phoneNumber);
            log.info("📱 [MODE DEV] SMS simulé vers {}: {}", phoneNumber, message);
            return;
        }
        
        try {
            initTwilio();
            
            Message twilioMessage = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(senderPhoneNumber),
                message
            ).create();
            
            log.info("✅ SMS envoyé avec succès à: {} - SID: {}", phoneNumber, twilioMessage.getSid());
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi du SMS à {}: {}", phoneNumber, e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi du SMS: " + e.getMessage());
        }
    }
    
    @Override
    public void sendOtpSms(String phoneNumber, String otpCode) {
        String message = String.format(
            "Votre code de vérification Pariba est: %s\n" +
            "Ce code expire dans 10 minutes.\n" +
            "Ne partagez ce code avec personne.",
            otpCode
        );
        
        sendSms(phoneNumber, message);
    }
    
    @Override
    public void sendWhatsApp(String phoneNumber, String message) {
        if (!smsEnabled) {
            log.warn("SMS n'est pas activé. WhatsApp non envoyé à: {}", phoneNumber);
            log.info("💬 [MODE DEV] WhatsApp simulé vers {}: {}", phoneNumber, message);
            return;
        }
        
        try {
            initTwilio();
            
            // Format WhatsApp: whatsapp:+22370123456
            String whatsappNumber = phoneNumber.startsWith("whatsapp:") ? phoneNumber : "whatsapp:" + phoneNumber;
            String whatsappFrom = senderPhoneNumber.startsWith("whatsapp:") ? senderPhoneNumber : "whatsapp:" + senderPhoneNumber;
            
            Message twilioMessage = Message.creator(
                new PhoneNumber(whatsappNumber),
                new PhoneNumber(whatsappFrom),
                message
            ).create();
            
            log.info("✅ WhatsApp envoyé avec succès à: {} - SID: {}", phoneNumber, twilioMessage.getSid());
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi WhatsApp à {}: {}", phoneNumber, e.getMessage());
            // Mode DEV: afficher dans les logs au lieu de lancer une exception
            log.warn("💬 [MODE DEV] WhatsApp simulé vers {}: {}", phoneNumber, message);
        }
    }
    
    @Override
    public void sendOtpWhatsApp(String phoneNumber, String otpCode) {
        String message = String.format(
            "🔐 *Pariba - Code de Vérification*\n\n" +
            "Votre code OTP est: *%s*\n\n" +
            "⏰ Ce code expire dans 10 minutes.\n" +
            "🔒 Ne partagez ce code avec personne.",
            otpCode
        );
        
        sendWhatsApp(phoneNumber, message);
    }
}
