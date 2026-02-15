package com.example.pariba.enums;

public enum NotificationType { 
    // Authentification et onboarding
    WELCOME_REGISTRATION("Bienvenue après inscription"),
    FIRST_LOGIN("Première connexion réussie"),                  // Première connexion réussie
    
    // OTP et sécurité
    OTP_VERIFICATION("Vérification OTP"),
    PASSWORD_RESET("Réinitialisation du mot de passe"),
    PASSWORD_CHANGED("Mot de passe modifié"),
    // Groupe - Création et gestion
    GROUP_CREATED("Groupe créé avec succès"),                  // Groupe créé avec succès
    GROUP_UPDATED("Groupe modifié"),                  // Groupe modifié
    GROUP_DELETED("Groupe supprimé"),                  // Groupe supprimé
    GROUP_INVITATION_SENT("Invitation envoyée"),          // Invitation envoyée
    GROUP_INVITATION_RECEIVED("Invitation reçue"),      // Invitation reçue
    GROUP_JOINED("Membre a rejoint le groupe"),                   // Membre a rejoint le groupe
    GROUP_LEFT("Membre a quitté le groupe"),                     // Membre a quitté le groupe
    MEMBER_ADDED("Nouveau membre ajouté"),                   // Nouveau membre ajouté
    MEMBER_REMOVED("Membre retiré du groupe"),                 // Membre retiré du groupe
    MEMBER_ROLE_CHANGED("Rôle du membre modifié"),            // Rôle du membre modifié
    
    // Demandes d'adhésion
    NEW_JOIN_REQUEST("Nouvelle demande d'adhésion"),          // Nouvelle demande d'adhésion (pour admin)
    JOIN_REQUEST_APPROVED("Demande d'adhésion approuvée"),    // Demande approuvée (pour demandeur)
    JOIN_REQUEST_REJECTED("Demande d'adhésion rejetée"),      // Demande rejetée (pour demandeur)

    // Tour - Rotation
    TOUR_STARTING_SOON("Tour commence bientôt (1-2 jours avant)"),             // Tour commence bientôt (1-2 jours avant)
    TOUR_STARTED("Tour a démarré (date début)"),                   // Tour a démarré (date début)
    TOUR_ENDING_SOON("Tour se termine bientôt"),               // Tour se termine bientôt
    TOUR_COMPLETED("Tour terminé (date fin)"),                 // Tour terminé (date fin)
    BENEFICIARY_SELECTED("Bénéficiaire sélectionné pour le tour"),           // Bénéficiaire sélectionné pour le tour
    YOUR_TURN_NEXT("C'est bientôt votre tour"),                 // C'est bientôt votre tour
    YOUR_TURN_NOW("C'est votre tour maintenant"),                  // C'est votre tour maintenant
    
    // Contributions et paiements
    CONTRIBUTION_DUE_SOON("Contribution due bientôt (2 jours avant)"),          // Contribution due bientôt (2 jours avant)
    CONTRIBUTION_DUE_TODAY("Contribution due aujourd'hui"),         // Contribution due aujourd'hui
    CONTRIBUTION_OVERDUE("Contribution en retard"),           // Contribution en retard
    CONTRIBUTION_REMINDER_1DAY("Rappel 1 jour avant échéance"),     // Rappel 1 jour avant échéance
    CONTRIBUTION_REMINDER_2DAYS("Rappel 2 jours avant échéance"),    // Rappel 2 jours avant échéance
    CONTRIBUTION_PAID("Contribution payée"),              // Contribution payée
    CONTRIBUTION_RECEIVED("Contribution reçue (pour admin)"),          // Contribution reçue (pour admin)
    PAYMENT_INITIATED("Paiement initié"),              // Paiement initié
    PAYMENT_SUCCESS("Paiement réussi"),                // Paiement réussi
    PAYMENT_FAILED("Paiement échoué"),                 // Paiement échoué
    PAYMENT_PENDING("Paiement en attente"),                // Paiement en attente
    PAYMENT_DECLARED("Paiement déclaré"),
    PAYMENT_DECLARATION_RECEIVED("Déclaration de paiement reçue"),
    PAYMENT_VALIDATED("Paiement validé"),
    PAYMENT_REJECTED("Paiement rejeté"),
    
    // Déboursement
    PAYOUT_READY("Déboursement prêt"),
    PAYOUT_PROCESSED("Déboursement effectué"),
    PAYOUT_RECEIVED("Déboursement reçu"),               // Déboursement reçu
    
    // Pénalités
    LATE_PENALTY_APPLIED("Pénalité de retard appliquée"),           // Pénalité de retard appliquée
    GRACE_PERIOD_ENDING("Période de grâce se termine"),            // Période de grâce se termine
    
    // Tontine - Cycle complet
    TONTINE_STARTING_SOON("Tontine commence bientôt"),          // Tontine commence bientôt
    TONTINE_STARTED("Tontine a démarré"),                // Tontine a démarré
    TONTINE_ENDING_SOON("Tontine se termine bientôt"),            // Tontine se termine bientôt
    TONTINE_COMPLETED("Tontine terminée"),              // Tontine terminée
    
    // Notifications admin
    ADMIN_ACTION_REQUIRED("Action admin requise"),          // Action admin requise
    ADMIN_REPORT_READY("Rapport admin prêt"),             // Rapport admin prêt
    
    // Exports et rapports
    EXPORT_READY("Export prêt"),
    REPORT_GENERATED("Rapport généré"),
    
    // Système
    SYSTEM_MAINTENANCE("Maintenance système"),
    SYSTEM_UPDATE("Mise à jour système");
     private final String label;

    NotificationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}