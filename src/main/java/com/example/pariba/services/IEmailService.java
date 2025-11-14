package com.example.pariba.services;

import java.util.Map;

/**
 * Service pour l'envoi d'emails
 */
public interface IEmailService {
    
    /**
     * Envoyer un email simple
     */
    void sendSimpleEmail(String to, String subject, String body);
    
    /**
     * Envoyer un email HTML
     */
    void sendHtmlEmail(String to, String subject, String htmlBody);
    
    /**
     * Envoyer un email avec template
     */
    void sendTemplateEmail(String to, String subject, String templateBody, Map<String, String> variables);
    
    /**
     * Envoyer un email à plusieurs destinataires
     */
    void sendBulkEmail(String[] to, String subject, String body);
}
