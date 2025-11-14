package com.example.pariba.services;

import java.util.List;
import java.util.Map;

/**
 * Service pour l'envoi de notifications push via Firebase Cloud Messaging
 */
public interface IPushNotificationService {
    
    /**
     * Envoyer une notification push à un seul appareil
     */
    void sendToDevice(String deviceToken, String title, String body, Map<String, String> data);
    
    /**
     * Envoyer une notification push à plusieurs appareils
     */
    void sendToMultipleDevices(List<String> deviceTokens, String title, String body, Map<String, String> data);
    
    /**
     * Envoyer une notification push à un topic
     */
    void sendToTopic(String topic, String title, String body, Map<String, String> data);
    
    /**
     * Souscrire des appareils à un topic
     */
    void subscribeToTopic(List<String> deviceTokens, String topic);
    
    /**
     * Désinscrire des appareils d'un topic
     */
    void unsubscribeFromTopic(List<String> deviceTokens, String topic);
}
