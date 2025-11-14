package com.example.pariba.services;

import com.example.pariba.dtos.responses.SubscriptionResponse;

/**
 * Interface du service de gestion des abonnements
 */
public interface ISubscriptionService {
    
    /**
     * Récupère l'abonnement actif d'une personne
     * @param personId L'identifiant de la personne
     * @return L'abonnement actif ou null
     */
    SubscriptionResponse getActiveSubscription(String personId);
    
    /**
     * Crée ou met à niveau un abonnement
     * @param personId L'identifiant de la personne
     * @param planId L'identifiant du plan
     * @return L'abonnement créé/mis à jour
     */
    SubscriptionResponse subscribe(String personId, String planId);
    
    /**
     * Annule un abonnement
     * @param personId L'identifiant de la personne
     */
    void cancelSubscription(String personId);
    
    /**
     * Vérifie si une personne a accès à une fonctionnalité premium
     * @param personId L'identifiant de la personne
     * @param feature La fonctionnalité à vérifier
     * @return true si la personne a accès
     */
    boolean hasFeatureAccess(String personId, String feature);
    
    /**
     * Renouvelle automatiquement les abonnements expirés
     */
    void renewExpiredSubscriptions();
}
