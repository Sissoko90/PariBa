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
        // Authentification
        createTemplate(NotificationType.WELCOME_REGISTRATION, NotificationChannel.EMAIL,
            "Bienvenue sur Pariba", getWelcomeEmailTemplate());
        createTemplate(NotificationType.FIRST_LOGIN, NotificationChannel.EMAIL,
            "Première connexion réussie", getFirstLoginEmailTemplate());
        
        // Groupe
        createTemplate(NotificationType.GROUP_CREATED, NotificationChannel.EMAIL,
            "Groupe créé avec succès", getGroupCreatedEmailTemplate());
        createTemplate(NotificationType.GROUP_INVITATION_RECEIVED, NotificationChannel.EMAIL,
            "Invitation à rejoindre {{groupe}}", getGroupInvitationEmailTemplate());
        createTemplate(NotificationType.GROUP_JOINED, NotificationChannel.EMAIL,
            "Nouveau membre dans {{groupe}}", getGroupJoinedEmailTemplate());
        
        // Tour
        createTemplate(NotificationType.TOUR_STARTING_SOON, NotificationChannel.EMAIL,
            "Tour {{tour}} commence bientôt", getTourStartingSoonEmailTemplate());
        createTemplate(NotificationType.TOUR_STARTED, NotificationChannel.EMAIL,
            "Tour {{tour}} a démarré", getTourStartedEmailTemplate());
        createTemplate(NotificationType.YOUR_TURN_NOW, NotificationChannel.EMAIL,
            "C'est votre tour!", getYourTurnNowEmailTemplate());
        
        // Contributions
        createTemplate(NotificationType.CONTRIBUTION_REMINDER_2DAYS, NotificationChannel.EMAIL,
            "Rappel: Contribution dans 2 jours", getContributionReminderEmailTemplate());
        createTemplate(NotificationType.CONTRIBUTION_DUE_TODAY, NotificationChannel.EMAIL,
            "Contribution due aujourd'hui", getContributionDueTodayEmailTemplate());
        createTemplate(NotificationType.CONTRIBUTION_PAID, NotificationChannel.EMAIL,
            "Contribution payée avec succès", getContributionPaidEmailTemplate());
        createTemplate(NotificationType.CONTRIBUTION_OVERDUE, NotificationChannel.EMAIL,
            "Contribution en retard", getContributionOverdueEmailTemplate());
        
        // Paiements
        createTemplate(NotificationType.PAYMENT_SUCCESS, NotificationChannel.EMAIL,
            "Paiement réussi - {{montant}} FCFA", getPaymentSuccessEmailTemplate());
        createTemplate(NotificationType.PAYOUT_PROCESSED, NotificationChannel.EMAIL,
            "Déboursement effectué - {{montant}} FCFA", getPayoutProcessedEmailTemplate());
    }
    
    private void createPushTemplates() {
        // Authentification
        createTemplate(NotificationType.WELCOME_REGISTRATION, NotificationChannel.PUSH,
            "Bienvenue sur Pariba!", "Bonjour {{prenom}}, bienvenue dans la communauté Pariba!");
        createTemplate(NotificationType.FIRST_LOGIN, NotificationChannel.PUSH,
            "Première connexion", "Félicitations {{prenom}}! Vous êtes maintenant connecté à Pariba.");
        
        // Groupe - Création et gestion
        createTemplate(NotificationType.GROUP_CREATED, NotificationChannel.PUSH,
            "Groupe créé", "Votre groupe {{groupe}} a été créé avec succès!");
        createTemplate(NotificationType.GROUP_INVITATION_RECEIVED, NotificationChannel.PUSH,
            "Nouvelle invitation", "Vous êtes invité à rejoindre {{groupe}}. Code: {{code}}");
        createTemplate(NotificationType.GROUP_JOINED, NotificationChannel.PUSH,
            "Nouveau membre", "{{prenom}} a rejoint le groupe {{groupe}}");
        createTemplate(NotificationType.MEMBER_ADDED, NotificationChannel.PUSH,
            "Membre ajouté", "{{prenom}} {{nom}} a été ajouté au groupe {{groupe}}");
        
        // Tour - Rotation
        createTemplate(NotificationType.TOUR_STARTING_SOON, NotificationChannel.PUSH,
            "Tour dans {{jours}} jours", "Le tour {{tour}} de {{groupe}} commence bientôt. Bénéficiaire: {{beneficiaire}}");
        createTemplate(NotificationType.TOUR_STARTED, NotificationChannel.PUSH,
            "Tour démarré", "Le tour {{tour}} de {{groupe}} a démarré! Bénéficiaire: {{beneficiaire}}");
        createTemplate(NotificationType.TOUR_COMPLETED, NotificationChannel.PUSH,
            "Tour terminé", "Le tour {{tour}} de {{groupe}} est terminé. Montant collecté: {{montant}} FCFA");
        createTemplate(NotificationType.YOUR_TURN_NEXT, NotificationChannel.PUSH,
            "Bientôt votre tour!", "Votre tour arrive dans {{groupe}}. Préparez-vous!");
        createTemplate(NotificationType.YOUR_TURN_NOW, NotificationChannel.PUSH,
            "C'est votre tour!", "Vous êtes le bénéficiaire du tour actuel de {{groupe}}!");
        
        // Contributions - Rappels
        createTemplate(NotificationType.CONTRIBUTION_REMINDER_2DAYS, NotificationChannel.PUSH,
            "Contribution dans 2 jours", "Rappel: Contribution de {{montant}} FCFA pour {{groupe}} due le {{date}}");
        createTemplate(NotificationType.CONTRIBUTION_REMINDER_1DAY, NotificationChannel.PUSH,
            "Contribution demain", "Rappel: Contribution de {{montant}} FCFA pour {{groupe}} due demain");
        createTemplate(NotificationType.CONTRIBUTION_DUE_TODAY, NotificationChannel.PUSH,
            "Contribution aujourd'hui", "Votre contribution de {{montant}} FCFA pour {{groupe}} est due aujourd'hui");
        createTemplate(NotificationType.CONTRIBUTION_OVERDUE, NotificationChannel.PUSH,
            "Contribution en retard", "Votre contribution de {{montant}} FCFA pour {{groupe}} est en retard");
        createTemplate(NotificationType.CONTRIBUTION_PAID, NotificationChannel.PUSH,
            "Contribution payée", "Votre contribution de {{montant}} FCFA a été payée avec succès");
        createTemplate(NotificationType.CONTRIBUTION_RECEIVED, NotificationChannel.PUSH,
            "Contribution reçue", "Contribution de {{montant}} FCFA reçue de {{prenom}} pour {{groupe}}");
        
        // Paiements
        createTemplate(NotificationType.PAYMENT_INITIATED, NotificationChannel.PUSH,
            "Paiement initié", "Votre paiement de {{montant}} FCFA est en cours de traitement");
        createTemplate(NotificationType.PAYMENT_SUCCESS, NotificationChannel.PUSH,
            "Paiement réussi", "Votre paiement de {{montant}} FCFA a été effectué avec succès");
        createTemplate(NotificationType.PAYMENT_FAILED, NotificationChannel.PUSH,
            "Paiement échoué", "Votre paiement de {{montant}} FCFA a échoué. Veuillez réessayer.");
        
        // Déboursement
        createTemplate(NotificationType.PAYOUT_READY, NotificationChannel.PUSH,
            "Déboursement prêt", "Votre déboursement de {{montant}} FCFA est prêt");
        createTemplate(NotificationType.PAYOUT_PROCESSED, NotificationChannel.PUSH,
            "Déboursement effectué", "Votre déboursement de {{montant}} FCFA a été traité");
        createTemplate(NotificationType.PAYOUT_RECEIVED, NotificationChannel.PUSH,
            "Déboursement reçu", "Vous avez reçu {{montant}} FCFA de {{groupe}}");
        
        // Pénalités
        createTemplate(NotificationType.LATE_PENALTY_APPLIED, NotificationChannel.PUSH,
            "Pénalité appliquée", "Une pénalité de {{penalite}} FCFA a été appliquée pour retard");
        createTemplate(NotificationType.GRACE_PERIOD_ENDING, NotificationChannel.PUSH,
            "Période de grâce", "La période de grâce se termine dans {{jours}} jours");
        
        // Tontine - Cycle
        createTemplate(NotificationType.TONTINE_STARTING_SOON, NotificationChannel.PUSH,
            "Tontine bientôt", "La tontine {{groupe}} commence le {{date}}");
        createTemplate(NotificationType.TONTINE_STARTED, NotificationChannel.PUSH,
            "Tontine démarrée", "La tontine {{groupe}} a officiellement démarré!");
        createTemplate(NotificationType.TONTINE_COMPLETED, NotificationChannel.PUSH,
            "Tontine terminée", "La tontine {{groupe}} est terminée. Merci à tous!");
        
        // Membres - Gestion
        createTemplate(NotificationType.MEMBER_REMOVED, NotificationChannel.PUSH,
            "Retiré du groupe", "Vous avez été retiré du groupe {{groupe}}");
        createTemplate(NotificationType.MEMBER_ROLE_CHANGED, NotificationChannel.PUSH,
            "Rôle modifié", "Votre rôle dans {{groupe}} a été modifié");
        
        // Système - Abonnements
        createTemplate(NotificationType.SYSTEM_UPDATE, NotificationChannel.PUSH,
            "Mise à jour", "Mise à jour de votre compte Pariba");
    }
    
    private void createSmsTemplates() {
        // Invitation SMS
        createTemplate(NotificationType.GROUP_INVITATION_RECEIVED, NotificationChannel.SMS, null,
            "Pariba: Vous êtes invité à rejoindre {{groupe}}. Contribution: {{montant}} FCFA. Code: {{code}}. Lien: {{lien}}");
        
        // Rappels de contribution
        createTemplate(NotificationType.CONTRIBUTION_REMINDER_2DAYS, NotificationChannel.SMS, null,
            "Pariba: Rappel - Contribution de {{montant}} FCFA pour {{groupe}} due dans 2 jours.");
        createTemplate(NotificationType.CONTRIBUTION_DUE_TODAY, NotificationChannel.SMS, null,
            "Pariba: URGENT - Votre contribution de {{montant}} FCFA pour {{groupe}} est due aujourd'hui!");
        createTemplate(NotificationType.CONTRIBUTION_OVERDUE, NotificationChannel.SMS, null,
            "Pariba: Contribution en retard! {{montant}} FCFA pour {{groupe}}. Pénalité possible.");
        
        // Paiements
        createTemplate(NotificationType.PAYMENT_SUCCESS, NotificationChannel.SMS, null,
            "Pariba: Paiement de {{montant}} FCFA effectué avec succès. Ref: {{reference}}");
        createTemplate(NotificationType.PAYOUT_RECEIVED, NotificationChannel.SMS, null,
            "Pariba: Vous avez reçu {{montant}} FCFA de {{groupe}}. Ref: {{reference}}");
    }
    
    private void createWhatsAppTemplates() {
        // Invitation WhatsApp
        createTemplate(NotificationType.GROUP_INVITATION_RECEIVED, NotificationChannel.WHATSAPP, null,
            "🎉 *Invitation Pariba*\n\n" +
            "Bonjour {{prenom}},\n\n" +
            "Vous êtes invité à rejoindre:\n*{{groupe}}*\n\n" +
            "📊 *Détails:*\n" +
            "• Contribution: {{montant}} FCFA\n" +
            "• Fréquence: {{frequence}}\n" +
            "• Membres: {{membres}}\n\n" +
            "🔑 Code: {{code}}\n" +
            "👉 Lien: {{lien}}\n\n" +
            "⏰ _Expire dans 24h_");
        
        // Tour
        createTemplate(NotificationType.YOUR_TURN_NOW, NotificationChannel.WHATSAPP, null,
            "🎉 *C'est votre tour!*\n\n" +
            "Félicitations {{prenom}}!\n\n" +
            "Vous êtes le bénéficiaire du tour actuel de *{{groupe}}*.\n\n" +
            "💰 Montant attendu: {{montant}} FCFA\n" +
            "📅 Date: {{date}}");
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
    
    private String getFirstLoginEmailTemplate() {
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
                    .footer { background: #f8f8f8; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✅ Première Connexion Réussie!</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour <strong>{{prenom}} {{nom}}</strong>,</p>
                        <p>Félicitations! Vous venez de vous connecter pour la première fois à Pariba.</p>
                        <p>Vous pouvez maintenant profiter de toutes les fonctionnalités de la plateforme.</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Pariba - Plateforme de Tontines</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
    
    private String getGroupCreatedEmailTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #667eea, #764ba2); color: white; padding: 30px; text-align: center; }
                    .content { padding: 30px; color: #333; line-height: 1.6; }
                    .footer { background: #f8f8f8; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Groupe Créé!</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour <strong>{{prenom}}</strong>,</p>
                        <p>Votre groupe <strong>{{groupe}}</strong> a été créé avec succès!</p>
                        <p><strong>Détails:</strong></p>
                        <ul>
                            <li>Contribution: {{montant}} FCFA</li>
                            <li>Fréquence: {{frequence}}</li>
                            <li>Nombre de tours: {{tours}}</li>
                        </ul>
                        <p>Vous pouvez maintenant inviter des membres à rejoindre votre groupe.</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Pariba - Plateforme de Tontines</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
    
    private String getGroupJoinedEmailTemplate() {
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
                    .footer { background: #f8f8f8; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>👥 Nouveau Membre!</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour,</p>
                        <p><strong>{{prenom}} {{nom}}</strong> a rejoint le groupe <strong>{{groupe}}</strong>!</p>
                        <p>Le groupe compte maintenant {{membres}} membres.</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Pariba - Plateforme de Tontines</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
    
    private String getTourStartingSoonEmailTemplate() {
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
                    .footer { background: #f8f8f8; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>⏰ Tour Bientôt!</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour,</p>
                        <p>Le tour <strong>{{tour}}</strong> du groupe <strong>{{groupe}}</strong> commence dans {{jours}} jours.</p>
                        <p><strong>Bénéficiaire:</strong> {{beneficiaire}}</p>
                        <p><strong>Date de début:</strong> {{date}}</p>
                        <p>Assurez-vous d'être prêt pour votre contribution!</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Pariba - Plateforme de Tontines</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
    
    private String getTourStartedEmailTemplate() {
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
                    .footer { background: #f8f8f8; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚀 Tour Démarré!</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour,</p>
                        <p>Le tour <strong>{{tour}}</strong> du groupe <strong>{{groupe}}</strong> a officiellement démarré!</p>
                        <p><strong>Bénéficiaire:</strong> {{beneficiaire}}</p>
                        <p><strong>Montant attendu:</strong> {{montant}} FCFA</p>
                        <p>Effectuez votre contribution dès maintenant.</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Pariba - Plateforme de Tontines</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
    
    private String getYourTurnNowEmailTemplate() {
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
                    .amount { font-size: 36px; color: #8b5cf6; font-weight: bold; text-align: center; margin: 20px 0; }
                    .footer { background: #f8f8f8; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 C'est Votre Tour!</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour <strong>{{prenom}}</strong>,</p>
                        <p>Félicitations! Vous êtes le bénéficiaire du tour actuel du groupe <strong>{{groupe}}</strong>!</p>
                        <div class="amount">{{montant}} FCFA</div>
                        <p>Vous recevrez le montant collecté une fois que tous les membres auront effectué leur contribution.</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Pariba - Plateforme de Tontines</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
    
    private String getContributionDueTodayEmailTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #ef4444, #dc2626); color: white; padding: 30px; text-align: center; }
                    .content { padding: 30px; color: #333; line-height: 1.6; }
                    .amount { font-size: 32px; color: #ef4444; font-weight: bold; text-align: center; margin: 20px 0; }
                    .button { display: inline-block; padding: 12px 30px; background: linear-gradient(135deg, #ef4444, #dc2626); color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { background: #f8f8f8; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔔 Contribution Due Aujourd'hui!</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour <strong>{{prenom}}</strong>,</p>
                        <p>Votre contribution pour le groupe <strong>{{groupe}}</strong> est due aujourd'hui!</p>
                        <div class="amount">{{montant}} FCFA</div>
                        <p>Effectuez votre paiement dès maintenant pour éviter les pénalités de retard.</p>
                        <p style="text-align: center;">
                            <a href="{{lien}}" class="button">Payer Maintenant</a>
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
    
    private String getContributionPaidEmailTemplate() {
        return getContributionReceivedEmailTemplate();
    }
    
    private String getContributionOverdueEmailTemplate() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #dc2626, #991b1b); color: white; padding: 30px; text-align: center; }
                    .content { padding: 30px; color: #333; line-height: 1.6; }
                    .amount { font-size: 32px; color: #dc2626; font-weight: bold; text-align: center; margin: 20px 0; }
                    .warning { background: #fef2f2; border-left: 4px solid #dc2626; padding: 15px; margin: 20px 0; }
                    .button { display: inline-block; padding: 12px 30px; background: linear-gradient(135deg, #dc2626, #991b1b); color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { background: #f8f8f8; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>❌ Contribution En Retard!</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour <strong>{{prenom}}</strong>,</p>
                        <p>Votre contribution pour le groupe <strong>{{groupe}}</strong> est en retard.</p>
                        <div class="amount">{{montant}} FCFA</div>
                        <div class="warning">
                            <strong>⚠️ Attention:</strong> Des pénalités de retard peuvent s'appliquer. Veuillez effectuer votre paiement immédiatement.
                        </div>
                        <p style="text-align: center;">
                            <a href="{{lien}}" class="button">Payer Immédiatement</a>
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
    
    private String getPaymentSuccessEmailTemplate() {
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
                    .amount { font-size: 36px; color: #10b981; font-weight: bold; text-align: center; margin: 20px 0; }
                    .success-badge { background: #d1fae5; color: #065f46; padding: 10px 20px; border-radius: 20px; display: inline-block; margin: 10px 0; }
                    .footer { background: #f8f8f8; padding: 20px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✅ Paiement Réussi!</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour <strong>{{prenom}}</strong>,</p>
                        <p>Votre paiement a été effectué avec succès!</p>
                        <div class="amount">{{montant}} FCFA</div>
                        <div style="text-align: center;">
                            <span class="success-badge">✓ Paiement Confirmé</span>
                        </div>
                        <p><strong>Détails:</strong></p>
                        <ul>
                            <li>Groupe: {{groupe}}</li>
                            <li>Date: {{date}}</li>
                            <li>Référence: {{reference}}</li>
                            <li>Méthode: {{methode}}</li>
                        </ul>
                        <p>Merci pour votre contribution!</p>
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
