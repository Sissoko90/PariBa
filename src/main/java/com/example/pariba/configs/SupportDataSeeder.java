package com.example.pariba.configs;

import com.example.pariba.enums.FAQCategory;
import com.example.pariba.enums.GuideCategory;
import com.example.pariba.models.FAQ;
import com.example.pariba.models.Guide;
import com.example.pariba.models.SupportContact;
import com.example.pariba.repositories.FAQRepository;
import com.example.pariba.repositories.GuideRepository;
import com.example.pariba.repositories.SupportContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SupportDataSeeder {
    
    @Bean
    CommandLineRunner initSupportData(FAQRepository faqRepository, GuideRepository guideRepository, 
                                      SupportContactRepository contactRepository) {
        return args -> {
            // Seeder Contact Support
            if (contactRepository.count() == 0) {
                log.info("🌱 Seeding Support Contact data...");
                SupportContact contact = new SupportContact();
                contact.setEmail("makenzyks6@gmail.com");
                contact.setPhone("+223 97 75 86 97");
                contact.setWhatsappNumber("+223 97 75 86 97");
                contact.setSupportHours("Lun-Ven: 8h-18h");
                contact.setActive(true);
                contactRepository.save(contact);
                log.info("✅ Support Contact data seeded successfully");
            }
            
            // Seeder FAQ
            if (faqRepository.count() == 0) {
                log.info("🌱 Seeding FAQ data...");
                
                // FAQ Général
                createFAQ(faqRepository, FAQCategory.GENERAL, 1,
                    "Qu'est-ce que PariBa ?",
                    "PariBa est une application mobile de gestion de tontines qui vous permet de créer, gérer et participer à des groupes de tontine de manière simple et sécurisée.");
                
                createFAQ(faqRepository, FAQCategory.GENERAL, 2,
                    "Comment créer un compte ?",
                    "Pour créer un compte, cliquez sur 'S'inscrire' sur la page de connexion, remplissez le formulaire avec vos informations personnelles et validez.");
                
                createFAQ(faqRepository, FAQCategory.GENERAL, 3,
                    "L'application est-elle gratuite ?",
                    "Oui, PariBa est entièrement gratuit. Aucun frais n'est prélevé sur vos transactions de tontine.");
                
                // FAQ Tontine
                createFAQ(faqRepository, FAQCategory.TONTINE, 1,
                    "Comment créer un groupe de tontine ?",
                    "Allez dans l'onglet 'Groupes', cliquez sur le bouton '+' et remplissez les informations du groupe (nom, montant, fréquence, etc.).");
                
                createFAQ(faqRepository, FAQCategory.TONTINE, 2,
                    "Comment rejoindre un groupe ?",
                    "Vous pouvez rejoindre un groupe en utilisant le code d'invitation fourni par le créateur du groupe ou en scannant le QR code.");
                
                createFAQ(faqRepository, FAQCategory.TONTINE, 3,
                    "Puis-je quitter un groupe ?",
                    "Oui, vous pouvez quitter un groupe à tout moment via les paramètres du groupe. Attention, vous perdrez l'accès aux informations du groupe.");
                
                // FAQ Paiement
                createFAQ(faqRepository, FAQCategory.PAYMENT, 1,
                    "Comment effectuer un paiement ?",
                    "Allez dans les détails du groupe, cliquez sur 'Payer' et suivez les instructions pour effectuer votre cotisation.");
                
                createFAQ(faqRepository, FAQCategory.PAYMENT, 2,
                    "Quels sont les modes de paiement acceptés ?",
                    "PariBa supporte les paiements par Mobile Money (Orange Money, Moov Money, etc.) et les virements bancaires.");
                
                createFAQ(faqRepository, FAQCategory.PAYMENT, 3,
                    "Que se passe-t-il si je rate un paiement ?",
                    "Si votre groupe a défini des pénalités de retard, elles seront appliquées après la période de grâce. Vous recevrez des notifications de rappel.");
                
                // FAQ Sécurité
                createFAQ(faqRepository, FAQCategory.SECURITY, 1,
                    "Mes données sont-elles sécurisées ?",
                    "Oui, toutes vos données sont cryptées et stockées de manière sécurisée. Nous ne partageons jamais vos informations personnelles.");
                
                createFAQ(faqRepository, FAQCategory.SECURITY, 2,
                    "Comment réinitialiser mon mot de passe ?",
                    "Sur la page de connexion, cliquez sur 'Mot de passe oublié' et suivez les instructions envoyées par email.");
                
                log.info("✅ FAQ data seeded successfully");
            }
            
            // Seeder Guides
            if (guideRepository.count() == 0) {
                log.info("🌱 Seeding Guide data...");
                
                // Guide Démarrage
                createGuide(guideRepository, GuideCategory.GETTING_STARTED, 1,
                    "Bienvenue sur PariBa",
                    "Découvrez comment démarrer avec PariBa",
                    "# Bienvenue sur PariBa !\n\n" +
                    "PariBa est votre application de gestion de tontines.\n\n" +
                    "## Premiers pas\n" +
                    "1. Créez votre compte\n" +
                    "2. Complétez votre profil\n" +
                    "3. Créez ou rejoignez un groupe\n\n" +
                    "C'est aussi simple que ça !",
                    "info", 2);
                
                createGuide(guideRepository, GuideCategory.ACCOUNT_MANAGEMENT, 2,
                    "Gérer votre profil",
                    "Comment modifier vos informations personnelles",
                    "# Gestion du profil\n\n" +
                    "## Modifier vos informations\n" +
                    "1. Allez dans Profil\n" +
                    "2. Cliquez sur 'Modifier le profil'\n" +
                    "3. Mettez à jour vos informations\n" +
                    "4. Enregistrez les modifications\n\n" +
                    "## Changer votre mot de passe\n" +
                    "1. Profil > Changer le mot de passe\n" +
                    "2. Entrez votre mot de passe actuel\n" +
                    "3. Entrez le nouveau mot de passe\n" +
                    "4. Confirmez",
                    "person", 3);
                
                createGuide(guideRepository, GuideCategory.TONTINE_CREATION, 3,
                    "Créer une tontine",
                    "Guide complet pour créer votre première tontine",
                    "# Créer une tontine\n\n" +
                    "## Étapes de création\n" +
                    "1. **Informations de base**\n" +
                    "   - Nom du groupe\n" +
                    "   - Description\n\n" +
                    "2. **Configuration financière**\n" +
                    "   - Montant de la cotisation\n" +
                    "   - Fréquence (hebdomadaire, mensuelle)\n" +
                    "   - Nombre de tours\n\n" +
                    "3. **Paramètres avancés**\n" +
                    "   - Pénalités de retard\n" +
                    "   - Période de grâce\n" +
                    "   - Mode de rotation\n\n" +
                    "4. **Inviter des membres**\n" +
                    "   - Partagez le code d'invitation\n" +
                    "   - Ou le QR code",
                    "group_add", 5);
                
                createGuide(guideRepository, GuideCategory.PAYMENTS, 4,
                    "Effectuer un paiement",
                    "Comment payer votre cotisation",
                    "# Effectuer un paiement\n\n" +
                    "## Méthode 1: Mobile Money\n" +
                    "1. Sélectionnez votre opérateur\n" +
                    "2. Entrez votre numéro\n" +
                    "3. Validez le paiement\n" +
                    "4. Confirmez sur votre téléphone\n\n" +
                    "## Méthode 2: Virement bancaire\n" +
                    "1. Notez les coordonnées bancaires\n" +
                    "2. Effectuez le virement\n" +
                    "3. Envoyez la preuve de paiement\n\n" +
                    "Votre paiement sera confirmé sous 24h.",
                    "payment", 4);
                
                log.info("✅ Guide data seeded successfully");
            }
        };
    }
    
    private void createFAQ(FAQRepository repository, FAQCategory category, int order, String question, String answer) {
        FAQ faq = new FAQ();
        faq.setCategory(category);
        faq.setDisplayOrder(order);
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faq.setActive(true);
        repository.save(faq);
    }
    
    private void createGuide(GuideRepository repository, GuideCategory category, int order, 
                            String title, String description, String content, String icon, int readTime) {
        Guide guide = new Guide();
        guide.setCategory(category);
        guide.setDisplayOrder(order);
        guide.setTitle(title);
        guide.setDescription(description);
        guide.setContent(content);
        guide.setIconName(icon);
        guide.setEstimatedReadTime(readTime);
        guide.setActive(true);
        repository.save(guide);
    }
}
