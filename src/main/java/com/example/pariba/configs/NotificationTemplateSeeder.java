package com.example.pariba.configs;

import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.models.NotificationTemplate;
import com.example.pariba.repositories.NotificationTemplateRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Seeder pour créer les templates de notifications par défaut
 */
@Component
@Order(2) // Après DataSeeder
@Slf4j
@Profile({"default","dev","prod"}) // ← inclure prod (ou supprime @Profile)                       // s’exécute tôt
@Transactional 
public class NotificationTemplateSeeder implements CommandLineRunner {
    
    private final NotificationTemplateRepository templateRepository;
    
    public NotificationTemplateSeeder(NotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }
    
    @Override
    public void run(String... args) {
        if (templateRepository.count() > 0) {
            log.info("Templates de notifications déjà existants - Seeding ignoré");
            return;
        }
        
        log.info("Création des templates de notifications par défaut...");
        
        // Templates Email
        createEmailTemplates();
        
        // Templates Push
        createPushTemplates();
        
        // Templates SMS
        createSmsTemplates();
        
        // Templates WhatsApp
        createWhatsAppTemplates();
        
        // Templates OTP
        createOtpTemplates();
        
        log.info("✅ {} templates de notifications créés", templateRepository.count());
    }
    
    private void createEmailTemplates() {
        // Bienvenue
        createTemplate(
            NotificationType.WELCOME,
            NotificationChannel.EMAIL,
            "Bienvenue sur Pariba",
            getWelcomeEmailTemplate()
        );
        
        // Contribution reçue
        createTemplate(
            NotificationType.CONTRIBUTION_RECEIVED,
            NotificationChannel.EMAIL,
            "Contribution reçue - {{montant}} FCFA",
            getContributionReceivedEmailTemplate()
        );
        
        // Paiement effectué
        createTemplate(
            NotificationType.PAYOUT_PROCESSED,
            NotificationChannel.EMAIL,
            "Paiement effectué - {{montant}} FCFA",
            getPayoutProcessedEmailTemplate()
        );
        
        // Invitation à un groupe
        createTemplate(
            NotificationType.GROUP_INVITATION,
            NotificationChannel.EMAIL,
            "Invitation à rejoindre {{groupe}}",
            getGroupInvitationEmailTemplate()
        );
        
        // Rappel de contribution
        createTemplate(
            NotificationType.CONTRIBUTION_REMINDER,
            NotificationChannel.EMAIL,
            "Rappel: Contribution à effectuer",
            getContributionReminderEmailTemplate()
        );
    }
    
    private void createPushTemplates() {
        // Bienvenue
        createTemplate(
            NotificationType.WELCOME,
            NotificationChannel.PUSH,
            "Bienvenue sur Pariba",
            "Bonjour {{prenom}}, bienvenue dans la communauté Pariba!"
        );
        
        // Contribution reçue
        createTemplate(
            NotificationType.CONTRIBUTION_RECEIVED,
            NotificationChannel.PUSH,
            "Contribution reçue",
            "Votre contribution de {{montant}} FCFA a été reçue avec succès."
        );
        
        // Paiement effectué
        createTemplate(
            NotificationType.PAYOUT_PROCESSED,
            NotificationChannel.PUSH,
            "Paiement effectué",
            "Votre paiement de {{montant}} FCFA a été traité."
        );
        
        // Invitation à un groupe
        createTemplate(
            NotificationType.GROUP_INVITATION,
            NotificationChannel.PUSH,
            "Nouvelle invitation",
            "Vous avez été invité à rejoindre {{groupe}}"
        );
        
        // Rappel de contribution
        createTemplate(
            NotificationType.CONTRIBUTION_REMINDER,
            NotificationChannel.PUSH,
            "Rappel de contribution",
            "N'oubliez pas votre contribution de {{montant}} FCFA"
        );
    }
    
    private void createSmsTemplates() {
        // Invitation SMS
        createTemplate(
            NotificationType.GROUP_INVITATION,
            NotificationChannel.SMS,
            null,
            "🎉 Vous êtes invité à rejoindre le groupe *{{groupe}}* sur Pariba!\n\n" +
            "Contribution: {{montant}} FCFA\n" +
            "Code d'invitation: {{code}}\n\n" +
            "Cliquez ici pour rejoindre: {{lien}}\n\n" +
            "⏰ Ce lien expire dans 24h"
        );
        
        // Rappel de contribution SMS
        createTemplate(
            NotificationType.CONTRIBUTION_REMINDER,
            NotificationChannel.SMS,
            null,
            "Rappel Pariba: Votre contribution de {{montant}} FCFA pour {{groupe}} est attendue. Merci!"
        );
    }
    
    private void createWhatsAppTemplates() {
        // Invitation WhatsApp
        createTemplate(
            NotificationType.GROUP_INVITATION,
            NotificationChannel.WHATSAPP,
            null,
            "🎉 *Invitation Pariba*\n\n" +
            "Bonjour {{prenom}},\n\n" +
            "Vous êtes invité à rejoindre le groupe:\n" +
            "*{{groupe}}*\n\n" +
            "📊 *Détails:*\n" +
            "• Contribution: {{montant}} FCFA\n" +
            "• Fréquence: {{frequence}}\n" +
            "• Membres actuels: {{membres}}\n\n" +
            "🔑 *Code d'invitation:* {{code}}\n\n" +
            "👉 Cliquez sur ce lien pour rejoindre:\n{{lien}}\n\n" +
            "⏰ _Ce lien expire dans 24h_\n\n" +
            "---\n" +
            "Pariba - Votre plateforme de tontine digitale"
        );
    }
    
    private void createOtpTemplates() {
        // OTP Email
        createTemplate(
            NotificationType.OTP_VERIFICATION,
            NotificationChannel.EMAIL,
            "Votre code de vérification Pariba",
            getOtpEmailTemplate()
        );
        
        // OTP SMS
        createTemplate(
            NotificationType.OTP_VERIFICATION,
            NotificationChannel.SMS,
            null,
            getOtpSmsTemplate()
        );
        
        // OTP WhatsApp
        createTemplate(
            NotificationType.OTP_VERIFICATION,
            NotificationChannel.WHATSAPP,
            null,
            getOtpWhatsAppTemplate()
        );
    }
    
    private String getOtpEmailTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .otp-code { background: #667eea; color: white; font-size: 32px; font-weight: bold; padding: 20px; text-align: center; border-radius: 8px; letter-spacing: 8px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Code de Vérification</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour,</p>
                        <p>Voici votre code de vérification Pariba :</p>
                        <div class="otp-code">{{code}}</div>
                        <p><strong>Ce code expire dans 10 minutes.</strong></p>
                        <p>Si vous n'avez pas demandé ce code, ignorez cet email.</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Pariba - Plateforme de Tontine Digitale</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
    
    private String getOtpSmsTemplate() {
        return "Votre code de vérification Pariba est: {{code}}\nCe code expire dans 10 minutes.\nNe partagez ce code avec personne.";
    }
    
    private String getOtpWhatsAppTemplate() {
        return "🔐 *Pariba - Code de Vérification*\n\nVotre code OTP est: *{{code}}*\n\n⏰ Ce code expire dans 10 minutes.\n🔒 Ne partagez ce code avec personne.";
    }
    
    private void createTemplate(NotificationType type, NotificationChannel channel, 
                                String subject, String body) {
        NotificationTemplate template = new NotificationTemplate();
        template.setType(type);
        template.setChannel(channel);
        template.setSubject(subject);
        template.setBodyTemplate(body);
        template.setActive(true);
        template.setLanguage("fr");
        
        templateRepository.save(template);
        log.info("Template créé: {} - {}", type, channel);
    }
    
    // Templates HTML pour emails
    
    private String getWelcomeEmailTemplate() {
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
                        <h1>🎉 Bienvenue sur Pariba!</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour <strong>{{prenom}} {{nom}}</strong>,</p>
                        <p>Nous sommes ravis de vous accueillir dans la communauté Pariba, votre plateforme de tontines digitales.</p>
                        <p>Avec Pariba, vous pouvez:</p>
                        <ul>
                            <li>Créer et gérer vos groupes de tontine</li>
                            <li>Effectuer des contributions en toute sécurité</li>
                            <li>Suivre vos paiements en temps réel</li>
                            <li>Recevoir des notifications automatiques</li>
                        </ul>
                        <p>Commencez dès maintenant votre expérience Pariba!</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Pariba - Plateforme de Tontines</p>
                        <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
    
    private String getContributionReceivedEmailTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #10b981, #059669); color: white; padding: 30px; text-align: center; }
                    .content { padding: 30px; color: #333; line-height: 1.6; }
                    .amount { font-size: 32px; color: #10b981; font-weight: bold; text-align: center; margin: 20px 0; }
                    .footer { background: #f8f8f8; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Contribution Reçue</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour <strong>{{prenom}}</strong>,</p>
                        <p>Votre contribution a été reçue avec succès!</p>
                        <div class="amount">{{montant}} FCFA</div>
                        <p><strong>Détails:</strong></p>
                        <ul>
                            <li>Groupe: {{groupe}}</li>
                            <li>Date: {{date}}</li>
                            <li>Référence: {{reference}}</li>
                        </ul>
                        <p>Merci de votre participation!</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Pariba - Plateforme de Tontines</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
    
    private String getPayoutProcessedEmailTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #3b82f6, #2563eb); color: white; padding: 30px; text-align: center; }
                    .content { padding: 30px; color: #333; line-height: 1.6; }
                    .amount { font-size: 32px; color: #3b82f6; font-weight: bold; text-align: center; margin: 20px 0; }
                    .footer { background: #f8f8f8; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Paiement Effectué</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour <strong>{{prenom}}</strong>,</p>
                        <p>Votre paiement a été traité avec succès!</p>
                        <div class="amount">{{montant}} FCFA</div>
                        <p><strong>Détails:</strong></p>
                        <ul>
                            <li>Groupe: {{groupe}}</li>
                            <li>Date: {{date}}</li>
                            <li>Méthode: {{methode}}</li>
                            <li>Référence: {{reference}}</li>
                        </ul>
                        <p>Le montant sera crédité sous 24-48h.</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Pariba - Plateforme de Tontines</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
    
    private String getGroupInvitationEmailTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #8b5cf6, #7c3aed); color: white; padding: 30px; text-align: center; }
                    .content { padding: 30px; color: #333; line-height: 1.6; }
                    .button { display: inline-block; padding: 12px 30px; background: linear-gradient(135deg, #8b5cf6, #7c3aed); color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { background: #f8f8f8; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Nouvelle Invitation</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour <strong>{{prenom}}</strong>,</p>
                        <p>Vous avez été invité à rejoindre le groupe:</p>
                        <h2 style="color: #8b5cf6; text-align: center;">{{groupe}}</h2>
                        <p><strong>Détails du groupe:</strong></p>
                        <ul>
                            <li>Contribution: {{montant}} FCFA</li>
                            <li>Fréquence: {{frequence}}</li>
                            <li>Membres: {{membres}}</li>
                        </ul>
                        <div style="background: #f3f4f6; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0;">
                            <p style="margin: 0; color: #666; font-size: 14px;">Code d'invitation</p>
                            <p style="margin: 5px 0 0 0; font-size: 24px; font-weight: bold; color: #8b5cf6; letter-spacing: 2px;">{{code}}</p>
                        </div>
                        <p style="text-align: center;">
                            <a href="{{lien}}" class="button" style="color: #ffffff;">Accepter l'invitation</a>
                        </p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Pariba - Plateforme de Tontines</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
    
    private String getContributionReminderEmailTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #f59e0b, #d97706); color: white; padding: 30px; text-align: center; }
                    .content { padding: 30px; color: #333; line-height: 1.6; }
                    .amount { font-size: 32px; color: #f59e0b; font-weight: bold; text-align: center; margin: 20px 0; }
                    .button { display: inline-block; padding: 12px 30px; background: linear-gradient(135deg, #f59e0b, #d97706); color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { background: #f8f8f8; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Rappel de Contribution</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour <strong>{{prenom}}</strong>,</p>
                        <p>Ceci est un rappel pour votre contribution au groupe <strong>{{groupe}}</strong>.</p>
                        <div class="amount">{{montant}} FCFA</div>
                        <p><strong>Date limite:</strong> {{date_limite}}</p>
                        <p>N'oubliez pas d'effectuer votre contribution avant la date limite.</p>
                        <p style="text-align: center;">
                            <a href="{{lien}}" class="button">Effectuer ma contribution</a>
                        </p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Pariba - Plateforme de Tontines</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}
