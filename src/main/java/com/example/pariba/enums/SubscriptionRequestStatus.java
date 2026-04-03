package com.example.pariba.enums;

/**
 * Statut des demandes d'abonnement
 */
public enum SubscriptionRequestStatus {
    PENDING,    // En attente de validation admin
    APPROVED,   // Approuvee par l'admin
    REJECTED,   // Rejetee par l'admin
    CANCELLED   // Annulee par l'utilisateur
}
