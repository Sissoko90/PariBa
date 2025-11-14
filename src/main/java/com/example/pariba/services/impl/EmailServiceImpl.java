package com.example.pariba.services.impl;

import com.example.pariba.services.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implémentation du service d'envoi d'emails
 */
@Service
@Slf4j
public class EmailServiceImpl implements IEmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.from:noreply@pariba.com}")
    private String fromEmail;
    
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @Override
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            log.info("Email simple envoyé à: {}", to);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email à {}: {}", to, e.getMessage());
            throw new RuntimeException("Échec de l'envoi de l'email", e);
        }
    }
    
    @Override
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML
            
            mailSender.send(message);
            log.info("Email HTML envoyé à: {}", to);
        } catch (MessagingException e) {
            log.error("Erreur lors de l'envoi de l'email HTML à {}: {}", to, e.getMessage());
            throw new RuntimeException("Échec de l'envoi de l'email HTML", e);
        }
    }
    
    @Override
    public void sendTemplateEmail(String to, String subject, String templateBody, Map<String, String> variables) {
        // Remplacer les variables dans le template
        String processedBody = templateBody;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            processedBody = processedBody.replace(placeholder, entry.getValue());
        }
        
        sendHtmlEmail(to, subject, processedBody);
    }
    
    @Override
    public void sendBulkEmail(String[] to, String subject, String body) {
        for (String recipient : to) {
            try {
                sendSimpleEmail(recipient, subject, body);
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi de l'email en masse à {}: {}", recipient, e.getMessage());
                // Continue avec les autres destinataires
            }
        }
    }
    
    /**
     * Créer un email HTML avec style
     */
    public String createStyledHtmlEmail(String title, String content, String buttonText, String buttonUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #667eea, #764ba2); color: white; padding: 30px; text-align: center; }
                    .header h1 { margin: 0; font-size: 28px; }
                    .content { padding: 30px; color: #333; line-height: 1.6; }
                    .button { display: inline-block; padding: 12px 30px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { background: #f8f8f8; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>%s</h1>
                    </div>
                    <div class="content">
                        %s
                        %s
                    </div>
                    <div class="footer">
                        <p>© 2025 Pariba - Plateforme de Tontines</p>
                        <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                title,
                content,
                buttonText != null && buttonUrl != null 
                    ? "<a href='" + buttonUrl + "' class='button'>" + buttonText + "</a>" 
                    : ""
            );
    }
}
