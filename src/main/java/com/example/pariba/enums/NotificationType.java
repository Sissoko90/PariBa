package com.example.pariba.enums;

public enum NotificationType { 
    // Authentification et onboarding
    WELCOME_REGISTRATION,           // Bienvenue après inscription
    FIRST_LOGIN,                    // Première connexion réussie
    
    // OTP et sécurité
    OTP_VERIFICATION,
    PASSWORD_RESET,
    PASSWORD_CHANGED,
    
    // Groupe - Création et gestion
    GROUP_CREATED,                  // Groupe créé avec succès
    GROUP_UPDATED,                  // Groupe modifié
    GROUP_DELETED,                  // Groupe supprimé
    GROUP_INVITATION_SENT,          // Invitation envoyée
    GROUP_INVITATION_RECEIVED,      // Invitation reçue
    GROUP_JOINED,                   // Membre a rejoint le groupe
    GROUP_LEFT,                     // Membre a quitté le groupe
    MEMBER_ADDED,                   // Nouveau membre ajouté
    MEMBER_REMOVED,                 // Membre retiré du groupe
    MEMBER_ROLE_CHANGED,            // Rôle du membre modifié
    
    // Tour - Rotation
    TOUR_STARTING_SOON,             // Tour commence bientôt (1-2 jours avant)
    TOUR_STARTED,                   // Tour a démarré (date début)
    TOUR_ENDING_SOON,               // Tour se termine bientôt
    TOUR_COMPLETED,                 // Tour terminé (date fin)
    BENEFICIARY_SELECTED,           // Bénéficiaire sélectionné pour le tour
    YOUR_TURN_NEXT,                 // C'est bientôt votre tour
    YOUR_TURN_NOW,                  // C'est votre tour maintenant
    
    // Contributions et paiements
    CONTRIBUTION_DUE_SOON,          // Contribution due bientôt (2 jours avant)
    CONTRIBUTION_DUE_TODAY,         // Contribution due aujourd'hui
    CONTRIBUTION_OVERDUE,           // Contribution en retard
    CONTRIBUTION_REMINDER_1DAY,     // Rappel 1 jour avant échéance
    CONTRIBUTION_REMINDER_2DAYS,    // Rappel 2 jours avant échéance
    CONTRIBUTION_PAID,              // Contribution payée
    CONTRIBUTION_RECEIVED,          // Contribution reçue (pour admin)
    PAYMENT_INITIATED,              // Paiement initié
    PAYMENT_SUCCESS,                // Paiement réussi
    PAYMENT_FAILED,                 // Paiement échoué
    PAYMENT_PENDING,                // Paiement en attente
    
    // Déboursement
    PAYOUT_READY,                   // Déboursement prêt
    PAYOUT_PROCESSED,               // Déboursement effectué
    PAYOUT_RECEIVED,                // Déboursement reçu
    
    // Pénalités
    LATE_PENALTY_APPLIED,           // Pénalité de retard appliquée
    GRACE_PERIOD_ENDING,            // Période de grâce se termine
    
    // Tontine - Cycle complet
    TONTINE_STARTING_SOON,          // Tontine commence bientôt
    TONTINE_STARTED,                // Tontine a démarré
    TONTINE_ENDING_SOON,            // Tontine se termine bientôt
    TONTINE_COMPLETED,              // Tontine terminée
    
    // Notifications admin
    ADMIN_ACTION_REQUIRED,          // Action admin requise
    ADMIN_REPORT_READY,             // Rapport admin prêt
    
    // Exports et rapports
    EXPORT_READY,
    REPORT_GENERATED,
    
    // Système
    SYSTEM_MAINTENANCE,
    SYSTEM_UPDATE
}