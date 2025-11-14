package com.example.pariba.services.impl;

import com.example.pariba.services.IPushNotificationService;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Implémentation du service de notifications push via Firebase Cloud Messaging
 */
@Service
@Slf4j
public class PushNotificationServiceImpl implements IPushNotificationService {
    
    @Override
    public void sendToDevice(String deviceToken, String title, String body, Map<String, String> data) {
        try {
            Message.Builder messageBuilder = Message.builder()
                    .setToken(deviceToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());
            
            // Ajouter les données personnalisées si présentes
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }
            
            // Configuration Android
            messageBuilder.setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                            .setSound("default")
                            .setColor("#667eea")
                            .build())
                    .build());
            
            // Configuration iOS
            messageBuilder.setApnsConfig(ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setSound("default")
                            .build())
                    .build());
            
            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            log.info("Notification push envoyée avec succès. Message ID: {}", response);
            
        } catch (FirebaseMessagingException e) {
            log.error("Erreur lors de l'envoi de la notification push: {}", e.getMessage());
            throw new RuntimeException("Échec de l'envoi de la notification push", e);
        }
    }
    
    @Override
    public void sendToMultipleDevices(List<String> deviceTokens, String title, String body, Map<String, String> data) {
        if (deviceTokens == null || deviceTokens.isEmpty()) {
            log.warn("Liste de tokens vide, aucune notification envoyée");
            return;
        }
        
        try {
            MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .addAllTokens(deviceTokens);
            
            // Ajouter les données personnalisées
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }
            
            // Configuration Android
            messageBuilder.setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                            .setSound("default")
                            .setColor("#667eea")
                            .build())
                    .build());
            
            // Configuration iOS
            messageBuilder.setApnsConfig(ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setSound("default")
                            .build())
                    .build());
            
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(messageBuilder.build());
            log.info("Notifications push envoyées: {} succès, {} échecs", 
                    response.getSuccessCount(), response.getFailureCount());
            
            // Logger les tokens qui ont échoué
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        log.error("Échec pour le token {}: {}", 
                                deviceTokens.get(i), 
                                responses.get(i).getException().getMessage());
                    }
                }
            }
            
        } catch (FirebaseMessagingException e) {
            log.error("Erreur lors de l'envoi des notifications push en masse: {}", e.getMessage());
            throw new RuntimeException("Échec de l'envoi des notifications push en masse", e);
        }
    }
    
    @Override
    public void sendToTopic(String topic, String title, String body, Map<String, String> data) {
        try {
            Message.Builder messageBuilder = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());
            
            // Ajouter les données personnalisées
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }
            
            // Configuration Android
            messageBuilder.setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                            .setSound("default")
                            .setColor("#667eea")
                            .build())
                    .build());
            
            // Configuration iOS
            messageBuilder.setApnsConfig(ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setSound("default")
                            .build())
                    .build());
            
            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            log.info("Notification push envoyée au topic '{}'. Message ID: {}", topic, response);
            
        } catch (FirebaseMessagingException e) {
            log.error("Erreur lors de l'envoi de la notification au topic '{}': {}", topic, e.getMessage());
            throw new RuntimeException("Échec de l'envoi de la notification au topic", e);
        }
    }
    
    @Override
    public void subscribeToTopic(List<String> deviceTokens, String topic) {
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .subscribeToTopic(deviceTokens, topic);
            log.info("Appareils souscrits au topic '{}': {} succès, {} échecs", 
                    topic, response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("Erreur lors de la souscription au topic '{}': {}", topic, e.getMessage());
            throw new RuntimeException("Échec de la souscription au topic", e);
        }
    }
    
    @Override
    public void unsubscribeFromTopic(List<String> deviceTokens, String topic) {
        try {
            TopicManagementResponse response = FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(deviceTokens, topic);
            log.info("Appareils désinscrits du topic '{}': {} succès, {} échecs", 
                    topic, response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("Erreur lors de la désinscription du topic '{}': {}", topic, e.getMessage());
            throw new RuntimeException("Échec de la désinscription du topic", e);
        }
    }
}
