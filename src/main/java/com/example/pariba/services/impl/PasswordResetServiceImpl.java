package com.example.pariba.services.impl;

import com.example.pariba.exceptions.BadRequestException;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.Person;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.services.IEmailService;
import com.example.pariba.services.IPasswordResetService;
import com.example.pariba.services.ISmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Implémentation du service de récupération de mot de passe
 */
@Service
@Slf4j
public class PasswordResetServiceImpl implements IPasswordResetService {
    
    private final PersonRepository personRepository;
    private final IEmailService emailService;
    private final ISmsService smsService;
    private final PasswordEncoder passwordEncoder;
    
    // Stockage temporaire des OTP (en production, utiliser Redis)
    private final Map<String, OtpData> otpStorage = new HashMap<>();
    
    // Pattern pour détecter un email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Pattern pour détecter un numéro de téléphone
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[0-9]{8,15}$"
    );
    
    public PasswordResetServiceImpl(PersonRepository personRepository,
                                   IEmailService emailService,
                                   ISmsService smsService,
                                   PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.emailService = emailService;
        this.smsService = smsService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public String sendPasswordResetOtp(String identifier) {
        // Déterminer si c'est un email ou un téléphone
        boolean isEmail = EMAIL_PATTERN.matcher(identifier).matches();
        boolean isPhone = PHONE_PATTERN.matcher(identifier).matches();
        
        if (!isEmail && !isPhone) {
            throw new BadRequestException("Format invalide. Veuillez fournir un email ou un numéro de téléphone valide.");
        }
        
        // Chercher l'utilisateur
        Person person;
        if (isEmail) {
            person = personRepository.findByEmail(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "email", identifier));
        } else {
            person = personRepository.findByPhone(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "phone", identifier));
        }
        
        // Générer un code OTP à 6 chiffres
        String otpCode = generateOtpCode();
        
        // Stocker l'OTP avec expiration de 10 minutes
        OtpData otpData = new OtpData(otpCode, LocalDateTime.now().plusMinutes(10), person.getId());
        otpStorage.put(identifier, otpData);
        
        // Envoyer l'OTP
        if (isEmail) {
            sendOtpByEmail(identifier, otpCode, person.getPrenom());
            log.info("✅ OTP envoyé par email à: {}", identifier);
        } else {
            sendOtpBySms(identifier, otpCode);
            log.info("✅ OTP envoyé par SMS à: {}", identifier);
        }
        
        // En dev, retourner le code (en prod, ne pas le retourner)
        return otpCode;
    }
    
    @Override
    public void resetPassword(String identifier, String otpCode, String newPassword) {
        // Vérifier l'OTP
        OtpData otpData = otpStorage.get(identifier);
        
        if (otpData == null) {
            throw new BadRequestException("Aucun code OTP trouvé pour cet identifiant");
        }
        
        if (LocalDateTime.now().isAfter(otpData.getExpiresAt())) {
            otpStorage.remove(identifier);
            throw new BadRequestException("Le code OTP a expiré. Veuillez en demander un nouveau.");
        }
        
        if (!otpData.getCode().equals(otpCode)) {
            throw new BadRequestException("Code OTP invalide");
        }
        
        // Récupérer la personne
        Person person = personRepository.findById(otpData.getPersonId())
            .orElseThrow(() -> new ResourceNotFoundException("Person", "id", otpData.getPersonId()));
        
        // Mettre à jour le mot de passe dans User
        if (person.getUser() == null) {
            throw new BadRequestException("Utilisateur non trouvé");
        }
        
        person.getUser().setPassword(passwordEncoder.encode(newPassword));
        personRepository.save(person);
        
        // Supprimer l'OTP utilisé
        otpStorage.remove(identifier);
        
        log.info("✅ Mot de passe réinitialisé pour: {}", identifier);
    }
    
    @Override
    public void changePassword(String personId, String oldPassword, String newPassword) {
        Person person = personRepository.findById(personId)
            .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));
        
        if (person.getUser() == null) {
            throw new BadRequestException("Utilisateur non trouvé");
        }
        
        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(oldPassword, person.getUser().getPassword())) {
            throw new BadRequestException("L'ancien mot de passe est incorrect");
        }
        
        // Mettre à jour le mot de passe
        person.getUser().setPassword(passwordEncoder.encode(newPassword));
        personRepository.save(person);
        
        log.info("✅ Mot de passe changé pour l'utilisateur: {}", personId);
    }
    
    /**
     * Générer un code OTP à 6 chiffres
     */
    private String generateOtpCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
    
    /**
     * Envoyer l'OTP par email
     */
    private void sendOtpByEmail(String email, String otpCode, String prenom) {
        String subject = "Réinitialisation de votre mot de passe Pariba";
        
        String htmlBody = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #667eea, #764ba2); color: white; padding: 30px; text-align: center; }
                    .content { padding: 30px; color: #333; line-height: 1.6; }
                    .otp-code { font-size: 36px; font-weight: bold; color: #667eea; text-align: center; padding: 20px; background: #f8f9fa; border-radius: 8px; margin: 20px 0; letter-spacing: 5px; }
                    .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 5px; }
                    .footer { background: #f8f8f8; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Réinitialisation de Mot de Passe</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour <strong>%s</strong>,</p>
                        <p>Vous avez demandé à réinitialiser votre mot de passe Pariba.</p>
                        <p>Voici votre code de vérification:</p>
                        <div class="otp-code">%s</div>
                        <div class="warning">
                            <strong>⚠️ Important:</strong>
                            <ul>
                                <li>Ce code expire dans <strong>10 minutes</strong></li>
                                <li>Ne partagez jamais ce code avec personne</li>
                                <li>Si vous n'avez pas demandé cette réinitialisation, ignorez cet email</li>
                            </ul>
                        </div>
                        <p>Entrez ce code dans l'application pour définir votre nouveau mot de passe.</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Pariba - Plateforme de Tontines</p>
                        <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
                    </div>
                </div>
            </body>
            </html>
            """, prenom, otpCode);
        
        emailService.sendHtmlEmail(email, subject, htmlBody);
    }
    
    /**
     * Envoyer l'OTP par SMS
     */
    private void sendOtpBySms(String phoneNumber, String otpCode) {
        smsService.sendOtpSms(phoneNumber, otpCode);
    }
    
    @Override
    public void sendResetPasswordEmail(String email) {
        log.info("📧 Envoi d'email de réinitialisation pour: {}", email);
        
        // Générer un token sécurisé
        String token = generateSecureToken();
        
        // Stocker le token avec expiration (30 minutes)
        Person person = personRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        ResetTokenData tokenData = new ResetTokenData(
            token,
            LocalDateTime.now().plusMinutes(30),
            person.getId()
        );
        resetTokenStorage.put(token, tokenData);
        
        // Envoyer l'email avec le lien de réinitialisation
        String resetLink = "http://localhost:8085/admin/reset-password?token=" + token;
        String subject = "Réinitialisation de votre mot de passe - Pariba Admin";
        String body = String.format(
            "Bonjour %s,\n\n" +
            "Vous avez demandé la réinitialisation de votre mot de passe.\n\n" +
            "Cliquez sur le lien suivant pour réinitialiser votre mot de passe:\n%s\n\n" +
            "Ce lien expirera dans 30 minutes.\n\n" +
            "Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.\n\n" +
            "Cordialement,\nL'équipe Pariba",
            person.getPrenom(),
            resetLink
        );
        
        emailService.sendHtmlEmail(email, subject, body);
        log.info("✅ Email de réinitialisation envoyé à: {}", email);
    }
    
    @Override
    public boolean validateResetToken(String token) {
        ResetTokenData tokenData = resetTokenStorage.get(token);
        
        if (tokenData == null) {
            log.warn("❌ Token non trouvé: {}", token);
            return false;
        }
        
        if (LocalDateTime.now().isAfter(tokenData.getExpiresAt())) {
            log.warn("❌ Token expiré: {}", token);
            resetTokenStorage.remove(token);
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean resetPassword(String token, String newPassword) {
        ResetTokenData tokenData = resetTokenStorage.get(token);
        
        if (tokenData == null || LocalDateTime.now().isAfter(tokenData.getExpiresAt())) {
            log.warn("❌ Token invalide ou expiré");
            return false;
        }
        
        try {
            // Récupérer la personne
            Person person = personRepository.findById(tokenData.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
            
            // Changer le mot de passe
            person.getUser().setPassword(passwordEncoder.encode(newPassword));
            personRepository.save(person);
            
            // Supprimer le token utilisé
            resetTokenStorage.remove(token);
            
            log.info("✅ Mot de passe réinitialisé pour: {}", person.getEmail());
            return true;
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la réinitialisation du mot de passe", e);
            return false;
        }
    }
    
    /**
     * Générer un token sécurisé
     */
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        
        StringBuilder token = new StringBuilder();
        for (byte b : bytes) {
            token.append(String.format("%02x", b));
        }
        return token.toString();
    }
    
    // Stockage temporaire des tokens de réinitialisation (en production, utiliser Redis)
    private final Map<String, ResetTokenData> resetTokenStorage = new HashMap<>();
    
    /**
     * Classe interne pour stocker les données de token de réinitialisation
     */
    private static class ResetTokenData {
        private final String token;
        private final LocalDateTime expiresAt;
        private final String personId;
        
        public ResetTokenData(String token, LocalDateTime expiresAt, String personId) {
            this.token = token;
            this.expiresAt = expiresAt;
            this.personId = personId;
        }
        
        public String getToken() { return token; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public String getPersonId() { return personId; }
    }
    
    /**
     * Classe interne pour stocker les données OTP
     */
    private static class OtpData {
        private final String code;
        private final LocalDateTime expiresAt;
        private final String personId;
        
        public OtpData(String code, LocalDateTime expiresAt, String personId) {
            this.code = code;
            this.expiresAt = expiresAt;
            this.personId = personId;
        }
        
        public String getCode() { return code; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public String getPersonId() { return personId; }
    }
}
