package com.example.pariba.services;

import com.example.pariba.dtos.requests.RegisterDeviceRequest;
import com.example.pariba.dtos.responses.DeviceTokenResponse;
import com.example.pariba.dtos.responses.DeviceResponse;

import java.util.List;

/**
 * Interface du service de gestion des tokens d'appareil (push notifications)
 */
public interface IDeviceTokenService {
    
    /**
     * Enregistre ou met à jour un token d'appareil
     * @param personId L'identifiant de la personne
     * @param request Les informations du token
     * @return Le token enregistré
     */
    DeviceTokenResponse registerDeviceToken(String personId, RegisterDeviceRequest request);
    
    /**
     * Récupère tous les tokens actifs d'une personne
     * @param personId L'identifiant de la personne
     * @return Liste des tokens actifs
     */
    List<DeviceTokenResponse> getActiveTokensByPerson(String personId);
    
    /**
     * Désactive un token d'appareil
     * @param tokenId L'identifiant du token
     * @param personId L'identifiant de la personne
     */
    void deactivateToken(String tokenId, String personId);
    
    /**
     * Nettoie les tokens expirés ou inactifs
     */
    void cleanupInactiveTokens();
    
    // Nouvelles méthodes pour la gestion mobile
    
    /**
     * Enregistre un nouvel appareil
     */
    DeviceResponse registerDevice(String personId, RegisterDeviceRequest request);
    
    /**
     * Récupère tous les appareils d'une personne
     */
    List<DeviceResponse> getDevicesByPerson(String personId);
    
    /**
     * Active un appareil
     */
    DeviceResponse activateDevice(String personId, String deviceId);
    
    /**
     * Désactive un appareil
     */
    DeviceResponse deactivateDevice(String personId, String deviceId);
    
    /**
     * Supprime un appareil
     */
    void deleteDevice(String personId, String deviceId);
    
    /**
     * Met à jour le token d'un appareil
     */
    DeviceResponse updateToken(String personId, String deviceId, String newToken);
}
