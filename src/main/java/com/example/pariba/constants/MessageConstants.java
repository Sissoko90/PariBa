package com.example.pariba.constants;

public class MessageConstants {
    
    // Messages de succès généraux
    public static final String SUCCESS_OPERATION = "Opération réussie";
    public static final String SUCCESS_CREATED = "Créé avec succès";
    public static final String SUCCESS_UPDATED = "Mis à jour avec succès";
    public static final String SUCCESS_DELETED = "Supprimé avec succès";
    
    // Authentification
    public static final String AUTH_SUCCESS_LOGIN = "Connexion réussie";
    public static final String AUTH_SUCCESS_REGISTER = "Inscription réussie";
    public static final String AUTH_SUCCESS_LOGOUT = "Déconnexion réussie";
    public static final String AUTH_ERROR_INVALID_CREDENTIALS = "Identifiants invalides";
    public static final String AUTH_ERROR_USER_EXISTS = "Cet utilisateur existe déjà";
    public static final String AUTH_ERROR_USER_NOT_FOUND = "Utilisateur non trouvé";
    public static final String AUTH_ERROR_UNAUTHORIZED = "Non autorisé";
    public static final String AUTH_ERROR_TOKEN_EXPIRED = "Token expiré";
    public static final String AUTH_ERROR_TOKEN_INVALID = "Token invalide";
    
    // OTP
    public static final String OTP_SUCCESS_SENT = "Code OTP envoyé avec succès";
    public static final String OTP_SUCCESS_VERIFIED = "Code OTP vérifié avec succès";
    public static final String OTP_ERROR_INVALID = "Code OTP invalide ou expiré";
    public static final String OTP_ERROR_EXPIRED = "Code OTP expiré";
    public static final String OTP_ERROR_ALREADY_USED = "Code OTP déjà utilisé";
    
    // Profil
    public static final String SUCCESS_PROFILE_UPDATED = "Profil mis à jour avec succès";
    public static final String SUCCESS_PHOTO_UPLOADED = "Photo uploadée avec succès";
    public static final String SUCCESS_PASSWORD_RESET = "Mot de passe réinitialisé avec succès";
    public static final String PROFILE_ERROR_NOT_FOUND = "Profil non trouvé";
    
    // Groupes
    public static final String SUCCESS_GROUP_CREATED = "Groupe créé avec succès";
    public static final String SUCCESS_GROUP_UPDATED = "Groupe mis à jour avec succès";
    public static final String SUCCESS_GROUP_DELETED = "Groupe supprimé avec succès";
    public static final String GROUP_ERROR_NOT_FOUND = "Groupe non trouvé";
    public static final String GROUP_ERROR_ALREADY_EXISTS = "Un groupe avec ce nom existe déjà";
    public static final String GROUP_ERROR_INSUFFICIENT_MEMBERS = "Nombre de membres insuffisant pour démarrer";
    public static final String GROUP_ERROR_ALREADY_STARTED = "Le groupe a déjà démarré";
    public static final String GROUP_ERROR_NOT_ADMIN = "Vous n'êtes pas administrateur de ce groupe";
    
    // Invitations
    public static final String SUCCESS_INVITATION_SENT = "Invitation envoyée avec succès";
    public static final String SUCCESS_INVITATION_ACCEPTED = "Invitation acceptée avec succès";
    public static final String INVITATION_SUCCESS_DECLINED = "Invitation refusée";
    public static final String INVITATION_ERROR_NOT_FOUND = "Invitation non trouvée";
    public static final String INVITATION_ERROR_EXPIRED = "Invitation expirée";
    public static final String INVITATION_ERROR_ALREADY_MEMBER = "Vous êtes déjà membre de ce groupe";
    public static final String INVITATION_ERROR_INVALID_CODE = "Code d'invitation invalide";
    
    // Membres
    public static final String MEMBER_SUCCESS_ADDED = "Membre ajouté avec succès";
    public static final String SUCCESS_MEMBER_REMOVED = "Membre retiré avec succès";
    public static final String SUCCESS_MEMBER_ROLE_UPDATED = "Rôle du membre mis à jour";
    public static final String MEMBER_ERROR_NOT_FOUND = "Membre non trouvé";
    public static final String MEMBER_ERROR_CANNOT_REMOVE_CREATOR = "Impossible de retirer le créateur du groupe";
    
    // Tours
    public static final String TOUR_SUCCESS_GENERATED = "Tours générés avec succès";
    public static final String TOUR_SUCCESS_STARTED = "Tour démarré avec succès";
    public static final String TOUR_SUCCESS_COMPLETED = "Tour complété avec succès";
    public static final String TOUR_ERROR_NOT_FOUND = "Tour non trouvé";
    public static final String TOUR_ERROR_NO_CURRENT = "Aucun tour en cours";
    public static final String TOUR_ERROR_NO_NEXT = "Aucun tour suivant";
    public static final String TOUR_ERROR_NO_MEMBERS = "Aucun membre dans le groupe";
    public static final String TOUR_ERROR_ALREADY_GENERATED = "Les tours ont déjà été générés";
    public static final String TOUR_ERROR_ALREADY_STARTED = "Le tour a déjà démarré";
    public static final String TOUR_ERROR_NOT_IN_PROGRESS = "Le tour n'est pas en cours";
    public static final String TOUR_ERROR_INSUFFICIENT_MEMBERS = "Nombre de membres insuffisant pour générer les tours";
    public static final String TOUR_ERROR_NOT_READY = "Le tour n'est pas prêt à démarrer";
    
    // Contributions
    public static final String CONTRIBUTION_SUCCESS_PAID = "Contribution payée avec succès";
    public static final String CONTRIBUTION_SUCCESS_WAIVED = "Contribution annulée";
    public static final String CONTRIBUTION_ERROR_NOT_FOUND = "Contribution non trouvée";
    public static final String CONTRIBUTION_ERROR_ALREADY_PAID = "Cette contribution a déjà été payée";
    public static final String CONTRIBUTION_ERROR_AMOUNT_MISMATCH = "Le montant ne correspond pas";
    
    // Paiements
    public static final String PAYMENT_SUCCESS_PROCESSED = "Paiement traité avec succès";
    public static final String PAYMENT_SUCCESS_PAYOUT = "Déboursement effectué avec succès";
    public static final String PAYMENT_ERROR_FAILED = "Échec du paiement";
    public static final String PAYMENT_ERROR_NOT_FOUND = "Paiement non trouvé";
    public static final String PAYMENT_ERROR_NOT_YOUR_CONTRIBUTION = "Cette contribution ne vous appartient pas";
    public static final String PAYMENT_ERROR_ALREADY_PAID = "Cette contribution a déjà été payée";
    public static final String PAYMENT_ERROR_INSUFFICIENT_FUNDS = "Fonds insuffisants";
    public static final String PAYMENT_ERROR_INVALID_AMOUNT = "Montant invalide";
    
    // Payouts
    public static final String PAYOUT_ERROR_TOUR_NOT_COMPLETED = "Le tour n'est pas terminé";
    public static final String PAYOUT_ERROR_ALREADY_PROCESSED = "Le payout a déjà été traité";
    
    // Délégations
    public static final String DELEGATION_SUCCESS_CREATED = "Procuration créée avec succès";
    public static final String DELEGATION_SUCCESS_APPROVED = "Procuration approuvée";
    public static final String DELEGATION_SUCCESS_REVOKED = "Procuration révoquée";
    public static final String DELEGATION_ERROR_NOT_FOUND = "Procuration non trouvée";
    public static final String DELEGATION_ERROR_NOT_MEMBERS = "Les deux personnes doivent être membres du groupe";
    public static final String DELEGATION_ERROR_NOT_DELEGATOR = "Vous n'êtes pas le délégateur";
    public static final String DELEGATION_ERROR_ALREADY_EXISTS = "Une procuration existe déjà pour cette période";
    public static final String DELEGATION_ERROR_INVALID_DATES = "Dates invalides";
    
    // Notifications
    public static final String NOTIFICATION_SUCCESS_SENT = "Notification envoyée";
    public static final String NOTIFICATION_SUCCESS_READ = "Notification marquée comme lue";
    public static final String NOTIFICATION_ERROR_NOT_FOUND = "Notification non trouvée";
    
    // Exports
    public static final String EXPORT_SUCCESS_REQUESTED = "Export demandé avec succès";
    public static final String EXPORT_SUCCESS_READY = "Export prêt au téléchargement";
    public static final String EXPORT_ERROR_NOT_FOUND = "Export non trouvé";
    public static final String EXPORT_ERROR_FAILED = "Échec de la génération de l'export";
    
    // Abonnements
    public static final String SUBSCRIPTION_SUCCESS_CREATED = "Abonnement créé avec succès";
    public static final String SUBSCRIPTION_SUCCESS_CANCELED = "Abonnement annulé";
    public static final String SUBSCRIPTION_ERROR_NOT_FOUND = "Abonnement non trouvé";
    public static final String SUBSCRIPTION_ERROR_ALREADY_ACTIVE = "Vous avez déjà un abonnement actif";
    
    // Appareils
    public static final String DEVICE_SUCCESS_REGISTERED = "Appareil enregistré avec succès";
    public static final String DEVICE_SUCCESS_UNREGISTERED = "Appareil désenregistré";
    
    // Erreurs générales
    public static final String ERROR_VALIDATION = "Erreur de validation des données";
    public static final String ERROR_INTERNAL = "Erreur interne du serveur";
    public static final String ERROR_NOT_FOUND = "Ressource non trouvée";
    public static final String ERROR_FORBIDDEN = "Accès interdit";
    public static final String ERROR_BAD_REQUEST = "Requête invalide";
    
    private MessageConstants() {
        // Classe utilitaire, constructeur privé
    }
}
