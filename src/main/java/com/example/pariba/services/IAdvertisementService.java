package com.example.pariba.services;

import com.example.pariba.dtos.responses.AdvertisementResponse;

import java.util.List;

/**
 * Interface du service de gestion des publicités
 */
public interface IAdvertisementService {
    
    /**
     * Récupère les publicités actives pour un placement donné
     * @param placement Le placement (banner, interstitial, native)
     * @param personId L'identifiant de la personne (pour le ciblage)
     * @return Liste des publicités actives
     */
    List<AdvertisementResponse> getActiveAdvertisements(String placement, String personId);
    
    /**
     * Enregistre une impression de publicité
     * @param adId L'identifiant de la publicité
     * @param personId L'identifiant de la personne
     */
    void recordImpression(String adId, String personId);
    
    /**
     * Enregistre un clic sur une publicité
     * @param adId L'identifiant de la publicité
     * @param personId L'identifiant de la personne
     */
    void recordClick(String adId, String personId);
    
    /**
     * Récupère une publicité par son ID
     * @param adId L'identifiant de la publicité
     * @return La publicité
     */
    AdvertisementResponse getAdvertisementById(String adId);
}
