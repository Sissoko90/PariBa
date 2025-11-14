package com.example.pariba.constants;

public class ValidationMessages {
    
    // Champs obligatoires
    public static final String REQUIRED_FIELD = "Ce champ est obligatoire";
    public static final String REQUIRED_PRENOM = "Le prénom est obligatoire";
    public static final String REQUIRED_NOM = "Le nom est obligatoire";
    public static final String REQUIRED_EMAIL = "L'email est obligatoire";
    public static final String REQUIRED_PHONE = "Le téléphone est obligatoire";
    public static final String REQUIRED_PASSWORD = "Le mot de passe est obligatoire";
    public static final String REQUIRED_USERNAME = "L'identifiant est obligatoire";
    public static final String REQUIRED_GROUP_NAME = "Le nom du groupe est obligatoire";
    public static final String REQUIRED_AMOUNT = "Le montant est obligatoire";
    public static final String REQUIRED_FREQUENCY = "La fréquence est obligatoire";
    public static final String REQUIRED_ROTATION_MODE = "Le mode de rotation est obligatoire";
    public static final String REQUIRED_TOTAL_TOURS = "Le nombre total de tours est obligatoire";
    public static final String REQUIRED_START_DATE = "La date de début est obligatoire";
    public static final String REQUIRED_GROUP_ID = "L'ID du groupe est obligatoire";
    public static final String REQUIRED_PERSON_ID = "L'ID de la personne est obligatoire";
    public static final String REQUIRED_TOUR_ID = "L'ID du tour est obligatoire";
    public static final String REQUIRED_CONTRIBUTION_ID = "L'ID de la contribution est obligatoire";
    public static final String REQUIRED_PAYMENT_TYPE = "Le type de paiement est obligatoire";
    public static final String REQUIRED_TARGET = "La cible (email/phone) est obligatoire";
    public static final String REQUIRED_OTP_CODE = "Le code OTP est obligatoire";
    public static final String REQUIRED_INVITATION_CODE = "Le code d'invitation est obligatoire";
    public static final String REQUIRED_NEW_ROLE = "Le nouveau rôle est obligatoire";
    public static final String REQUIRED_PROXY_ID = "L'ID du mandataire (proxy) est obligatoire";
    public static final String REQUIRED_VALID_FROM = "La date de début est obligatoire";
    public static final String REQUIRED_VALID_TO = "La date de fin est obligatoire";
    public static final String REQUIRED_REFRESH_TOKEN = "Le token de rafraîchissement est obligatoire";
    public static final String REQUIRED_FORMAT = "Le format est obligatoire";
    public static final String REQUIRED_TOKEN = "Le token FCM/APNs est obligatoire";
    public static final String REQUIRED_PLATFORM = "La plateforme est obligatoire";
    public static final String REQUIRED_SHUFFLE = "Le flag de shuffle est obligatoire";
    
    // Formats invalides
    public static final String INVALID_EMAIL = "Format d'email invalide";
    public static final String INVALID_PHONE = "Format de téléphone malien invalide (ex: +22376714142)";
    public static final String INVALID_FORMAT = "Format invalide";
    public static final String INVALID_PLATFORM = "La plateforme doit être 'ios' ou 'android'";
    public static final String INVALID_EXPORT_FORMAT = "Le format doit être PDF ou XLSX";
    
    // Tailles
    public static final String SIZE_PRENOM = "Le prénom doit contenir entre 2 et 50 caractères";
    public static final String SIZE_NOM = "Le nom doit contenir entre 2 et 50 caractères";
    public static final String SIZE_GROUP_NAME = "Le nom doit contenir entre 3 et 100 caractères";
    public static final String SIZE_DESCRIPTION = "La description ne peut pas dépasser 2048 caractères";
    public static final String SIZE_PASSWORD = "Le mot de passe doit contenir au moins 8 caractères";
    public static final String SIZE_OTP_CODE = "Le code OTP doit contenir entre 6 et 8 caractères";
    
    // Valeurs minimales/maximales
    public static final String MIN_AMOUNT = "Le montant minimum est 1000 FCFA";
    public static final String MIN_AMOUNT_PAYMENT = "Le montant minimum est 1 FCFA";
    public static final String MIN_TOURS = "Le nombre minimum de tours est 2";
    public static final String MAX_TOURS = "Le nombre maximum de tours est 100";
    public static final String MIN_GRACE_DAYS = "Les jours de grâce ne peuvent pas être négatifs";
    public static final String MAX_GRACE_DAYS = "Le maximum de jours de grâce est 30";
    public static final String MIN_PENALTY = "La pénalité ne peut pas être négative";
    
    // Dates
    public static final String FUTURE_DATE = "La date doit être dans le futur";
    public static final String FUTURE_START_DATE = "La date de début doit être dans le futur";
    public static final String FUTURE_END_DATE = "La date de fin doit être dans le futur";
    
    private ValidationMessages() {
        // Classe utilitaire, constructeur privé
    }
}
