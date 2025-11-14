package com.example.pariba.services.impl;

import com.example.pariba.constants.AppConstants;
import com.example.pariba.constants.MessageConstants;
import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.exceptions.BadRequestException;
import com.example.pariba.models.NotificationTemplate;
import com.example.pariba.models.OtpToken;
import com.example.pariba.repositories.NotificationTemplateRepository;
import com.example.pariba.repositories.OtpTokenRepository;
import com.example.pariba.services.IEmailService;
import com.example.pariba.services.IOtpService;
import com.example.pariba.services.ISmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class OtpServiceImpl implements IOtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final ISmsService smsService;
    private final IEmailService emailService;
    private final NotificationTemplateRepository templateRepository;
    private final Random random = new Random();

    public OtpServiceImpl(OtpTokenRepository otpTokenRepository,
                         ISmsService smsService,
                         IEmailService emailService,
                         NotificationTemplateRepository templateRepository) {
        this.otpTokenRepository = otpTokenRepository;
        this.smsService = smsService;
        this.emailService = emailService;
        this.templateRepository = templateRepository;
    }

    @Transactional
    public String generateAndSendOtp(String target) {
        return generateAndSendOtp(target, null);
    }
    
    @Transactional
    public String generateAndSendOtp(String target, com.example.pariba.enums.NotificationChannel channel) {
        // Générer un code OTP
        String code = generateOtpCode();

        // Créer le token OTP
        OtpToken otpToken = new OtpToken();
        otpToken.setTarget(target);
        otpToken.setCode(code);
        otpToken.setExpiresAt(Instant.now().plusSeconds(AppConstants.OTP_EXPIRATION_MINUTES * 60));
        otpToken.setUsed(false);
        otpTokenRepository.save(otpToken);

        // Envoyer selon le canal spécifié ou auto-détection
        if (channel != null) {
            sendOtpByChannel(target, code, channel);
        } else {
            sendOtpToTarget(target, code);
        }

        log.info("✅ OTP généré et envoyé pour: {} via {}", target, channel != null ? channel : "AUTO");
        return code;
    }
    
    /**
     * Envoyer l'OTP par un canal spécifique
     */
    private void sendOtpByChannel(String target, String code, com.example.pariba.enums.NotificationChannel channel) {
        switch (channel) {
            case EMAIL -> sendOtpByEmail(target, code);
            case SMS -> sendOtpBySms(target, code);
            case WHATSAPP -> smsService.sendOtpWhatsApp(target, code);
            case PUSH -> {
                log.warn("⚠️ PUSH notification non supporté pour OTP");
                throw new com.example.pariba.exceptions.BadRequestException("Canal PUSH non supporté pour l'envoi d'OTP");
            }
        }
    }
    
    /**
     * Envoyer l'OTP au target (email ou téléphone) - auto-détection
     */
    private void sendOtpToTarget(String target, String code) {
        if (isEmail(target)) {
            sendOtpByEmail(target, code);
        } else {
            sendOtpBySms(target, code);
        }
    }
    
    /**
     * Vérifier si le target est un email
     */
    private boolean isEmail(String target) {
        return target != null && target.contains("@");
    }
    
    /**
     * Envoyer l'OTP par email
     */
    private void sendOtpByEmail(String email, String code) {
        // Récupérer le template depuis la base de données
        NotificationTemplate template = templateRepository
            .findByTypeAndChannelAndActiveTrue(NotificationType.OTP_VERIFICATION, NotificationChannel.EMAIL)
            .orElse(null);
        
        String subject;
        String htmlBody;
        
        if (template != null) {
            // Utiliser le template de la base de données
            subject = template.getSubject();
            htmlBody = renderTemplate(template.getBodyTemplate(), Map.of("code", code));
            log.info("📧 Utilisation du template OTP depuis la base de données");
        } else {
            // Fallback: template par défaut
            subject = "Votre code de vérification Pariba";
            htmlBody = getFallbackOtpEmailTemplate(code);
            log.warn("⚠️ Template OTP non trouvé, utilisation du template par défaut");
        }
        
        try {
            emailService.sendHtmlEmail(email, subject, htmlBody);
            log.info("✅ OTP envoyé par email à: {}", email);
        } catch (Exception e) {
            // En mode développement, afficher l'OTP dans les logs si l'envoi échoue
            log.warn("⚠️ Impossible d'envoyer l'email (SMTP non configuré). Mode DEV activé.");
            log.info("📧 [MODE DEV] OTP pour {}: {}", email, code);
            log.info("💡 Pour activer l'envoi réel, configurez SMTP dans application.yml");
        }
    }
    
    /**
     * Envoyer l'OTP par SMS
     */
    private void sendOtpBySms(String phoneNumber, String code) {
        // Récupérer le template depuis la base de données
        NotificationTemplate template = templateRepository
            .findByTypeAndChannelAndActiveTrue(NotificationType.OTP_VERIFICATION, NotificationChannel.SMS)
            .orElse(null);
        
        String message;
        if (template != null) {
            message = renderTemplate(template.getBodyTemplate(), Map.of("code", code));
            log.info("📱 Utilisation du template OTP SMS depuis la base de données");
        } else {
            message = "Votre code de vérification Pariba est: " + code + "\nCe code expire dans 10 minutes.";
            log.warn("⚠️ Template OTP SMS non trouvé, utilisation du template par défaut");
        }
        
        try {
            smsService.sendSms(phoneNumber, message);
            log.info("✅ OTP envoyé par SMS à: {}", phoneNumber);
        } catch (Exception e) {
            // En mode développement, afficher l'OTP dans les logs si l'envoi échoue
            log.warn("⚠️ Impossible d'envoyer le SMS (Twilio non configuré). Mode DEV activé.");
            log.info("📱 [MODE DEV] OTP pour {}: {}", phoneNumber, code);
            log.info("💡 Pour activer l'envoi réel, configurez Twilio dans application.yml");
        }
    }

    @Transactional
    public boolean verifyOtp(String target, String code) {
        OtpToken otpToken = otpTokenRepository
                .findByTargetAndCodeAndUsedFalseAndExpiresAtAfter(target, code, Instant.now())
                .orElseThrow(() -> new BadRequestException(MessageConstants.OTP_ERROR_INVALID));

        // Marquer comme utilisé
        otpToken.setUsed(true);
        otpTokenRepository.save(otpToken);

        return true;
    }

    private String generateOtpCode() {
        int code = 100000 + random.nextInt(900000); // Code à 6 chiffres
        return String.valueOf(code);
    }

    @Transactional
    public void cleanupExpiredOtps() {
        otpTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
    
    /**
     * Rendre un template en remplaçant les placeholders
     */
    private String renderTemplate(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
    
    /**
     * Template de fallback si le template n'est pas trouvé dans la base de données
     */
    private String getFallbackOtpEmailTemplate(String code) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .otp-code { background: #667eea; color: white; font-size: 32px; font-weight: bold; padding: 20px; text-align: center; border-radius: 8px; letter-spacing: 8px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Code de Vérification</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour,</p>
                        <p>Voici votre code de vérification Pariba :</p>
                        <div class="otp-code">%s</div>
                        <p><strong>⏰ Ce code expire dans 10 minutes.</strong></p>
                        <p>Si vous n'avez pas demandé ce code, ignorez cet email.</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Pariba - Plateforme de Tontine Digitale</p>
                    </div>
                </div>
            </body>
            </html>
            """, code);
    }
}
