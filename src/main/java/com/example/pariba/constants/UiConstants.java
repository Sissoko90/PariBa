package com.example.pariba.constants;

/**
 * Constantes pour les messages UI (Controllers, Thymeleaf)
 * Centralise tous les textes bruts utilisés dans l'application
 */
public class UiConstants {
    
    // ============================================
    // MESSAGES DE SUCCÈS - API REST
    // ============================================
    
    // Publicités
    public static final String SUCCESS_ADS_RETRIEVED = "Publicités récupérées avec succès";
    public static final String SUCCESS_AD_RETRIEVED = "Publicité récupérée avec succès";
    public static final String SUCCESS_IMPRESSION_RECORDED = "Impression enregistrée";
    public static final String SUCCESS_CLICK_RECORDED = "Clic enregistré";
    
    // Tokens d'appareil
    public static final String SUCCESS_TOKEN_REGISTERED = "Token enregistré avec succès";
    public static final String SUCCESS_TOKENS_RETRIEVED = "Tokens récupérés avec succès";
    public static final String SUCCESS_TOKEN_DEACTIVATED = "Token désactivé avec succès";
    
    // Abonnements
    public static final String SUCCESS_SUBSCRIPTION_RETRIEVED = "Abonnement récupéré avec succès";
    public static final String SUCCESS_NO_ACTIVE_SUBSCRIPTION = "Aucun abonnement actif";
    public static final String SUCCESS_SUBSCRIPTION_CREATED = "Abonnement créé/mis à niveau avec succès";
    public static final String SUCCESS_SUBSCRIPTION_CANCELLED = "Abonnement annulé avec succès";
    
    // Documents
    public static final String SUCCESS_DOCUMENT_RETRIEVED = "Document récupéré avec succès";
    public static final String SUCCESS_DOCUMENTS_RETRIEVED = "Documents récupérés avec succès";
    public static final String SUCCESS_DOCUMENT_DELETED = "Document supprimé avec succès";
    
    // ============================================
    // DASHBOARD ADMIN - THYMELEAF
    // ============================================
    
    // Titres de pages
    public static final String PAGE_TITLE_DASHBOARD = "Dashboard Administrateur";
    public static final String PAGE_TITLE_USERS = "Gestion des Utilisateurs";
    public static final String PAGE_TITLE_GROUPS = "Gestion des Groupes";
    public static final String PAGE_TITLE_PAYMENTS = "Gestion des Paiements";
    public static final String PAGE_TITLE_REPORTS = "Rapports et Statistiques";
    public static final String PAGE_TITLE_SETTINGS = "Paramètres Système";
    
    // Labels statistiques
    public static final String LABEL_TOTAL_USERS = "Total Utilisateurs";
    public static final String LABEL_ACTIVE_GROUPS = "Groupes Actifs";
    public static final String LABEL_MONTHLY_PAYMENTS = "Paiements (Mois)";
    public static final String LABEL_TOTAL_VOLUME = "Volume Total";
    
    // Labels graphiques
    public static final String CHART_PAYMENTS_EVOLUTION = "Évolution des Paiements";
    public static final String CHART_GROUPS_DISTRIBUTION = "Répartition Groupes";
    public static final String CHART_LABEL_PAYMENTS = "Paiements (FCFA)";
    
    // Labels tableaux
    public static final String TABLE_RECENT_PAYMENTS = "Paiements Récents";
    public static final String TABLE_RECENT_GROUPS = "Groupes Récents";
    public static final String TABLE_COL_USER = "Utilisateur";
    public static final String TABLE_COL_AMOUNT = "Montant";
    public static final String TABLE_COL_TYPE = "Type";
    public static final String TABLE_COL_STATUS = "Statut";
    public static final String TABLE_COL_NAME = "Nom";
    public static final String TABLE_COL_MEMBERS = "Membres";
    
    // Boutons
    public static final String BTN_VIEW_ALL = "Voir tout";
    public static final String BTN_ADD = "Ajouter";
    public static final String BTN_EDIT = "Modifier";
    public static final String BTN_DELETE = "Supprimer";
    public static final String BTN_EXPORT = "Exporter";
    public static final String BTN_SEARCH = "Rechercher";
    public static final String BTN_FILTER = "Filtrer";
    public static final String BTN_SAVE = "Enregistrer";
    public static final String BTN_CANCEL = "Annuler";
    
    // Statuts
    public static final String STATUS_ACTIVE = "Actif";
    public static final String STATUS_INACTIVE = "Inactif";
    public static final String STATUS_PENDING = "En attente";
    public static final String STATUS_COMPLETED = "Terminé";
    public static final String STATUS_SUCCESS = "Succès";
    public static final String STATUS_FAILED = "Échec";
    
    // Messages d'erreur UI
    public static final String ERROR_LOADING_DATA = "Erreur lors du chargement des données";
    public static final String ERROR_SAVING_DATA = "Erreur lors de l'enregistrement";
    public static final String ERROR_DELETING_DATA = "Erreur lors de la suppression";
    public static final String ERROR_UNAUTHORIZED_ACCESS = "Accès non autorisé";
    
    // Messages de confirmation
    public static final String CONFIRM_DELETE = "Êtes-vous sûr de vouloir supprimer cet élément ?";
    public static final String CONFIRM_DEACTIVATE = "Êtes-vous sûr de vouloir désactiver cet élément ?";
    
    // Mois (pour graphiques)
    public static final String[] MONTHS_SHORT = {
        "Jan", "Fév", "Mar", "Avr", "Mai", "Jun", 
        "Jul", "Aoû", "Sep", "Oct", "Nov", "Déc"
    };
    
    public static final String[] MONTHS_FULL = {
        "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    };
    
    // Navigation
    public static final String NAV_DASHBOARD = "Tableau de bord";
    public static final String NAV_USERS = "Utilisateurs";
    public static final String NAV_GROUPS = "Groupes";
    public static final String NAV_PAYMENTS = "Paiements";
    public static final String NAV_REPORTS = "Rapports";
    public static final String NAV_SETTINGS = "Paramètres";
    public static final String NAV_LOGOUT = "Déconnexion";
    
    private UiConstants() {
        // Classe utilitaire, constructeur privé
    }
}
