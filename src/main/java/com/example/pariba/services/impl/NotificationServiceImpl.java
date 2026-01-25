package com.example.pariba.services.impl;

import com.example.pariba.dtos.responses.NotificationResponse;
import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.Notification;
import com.example.pariba.models.Person;
import com.example.pariba.repositories.DeviceTokenRepository;
import com.example.pariba.repositories.NotificationRepository;
import com.example.pariba.repositories.NotificationTemplateRepository;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.services.IEmailService;
import com.example.pariba.services.INotificationService;
import com.example.pariba.services.IPushNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final PersonRepository personRepository;
    private final NotificationTemplateRepository templateRepository;
    private final IEmailService emailService;
    private final IPushNotificationService pushService;
    private final DeviceTokenRepository deviceTokenRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                  PersonRepository personRepository,
                                  NotificationTemplateRepository templateRepository,
                                  IEmailService emailService,
                                  IPushNotificationService pushService,
                                  DeviceTokenRepository deviceTokenRepository) {
        this.notificationRepository = notificationRepository;
        this.personRepository = personRepository;
        this.templateRepository = templateRepository;
        this.emailService = emailService;
        this.pushService = pushService;
        this.deviceTokenRepository = deviceTokenRepository;
    }

    @Override
    public void sendNotification(String personId, NotificationType type, String title, 
                                String message, NotificationChannel channel) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));

        Notification notification = new Notification();
        notification.setPerson(person);
        notification.setType(type);
        notification.setTitle(title);
        notification.setBody(message);
        notification.setChannel(channel);
        notification.setReadFlag(false);

        notificationRepository.save(notification);

        // TODO: Envoyer la notification via le canal approprié (SMS, Email, Push)
        sendViaChannel(notification, channel);
    }
    
    @Override
    public void sendNotificationWithTemplate(String personId, NotificationType type, 
                                            NotificationChannel channel, Map<String, String> variables) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));
        
        // Récupérer le template
        var template = templateRepository.findByTypeAndChannelAndActiveTrue(type, channel)
                .orElse(null);
        
        if (template == null) {
            log.warn("Aucun template trouvé pour type={}, channel={}", type, channel);
            return;
        }
        
        // Ajouter les informations de la personne aux variables
        Map<String, String> allVariables = new HashMap<>(variables);
        allVariables.put("prenom", person.getPrenom() != null ? person.getPrenom() : "");
        allVariables.put("nom", person.getNom() != null ? person.getNom() : "");
        
        // Remplacer les variables dans le template
        String subject = replaceVariables(template.getSubject(), allVariables);
        String body = replaceVariables(template.getBodyTemplate(), allVariables);
        
        log.info("Envoi notification {} via {} à {}: {}", type, channel, person.getPhone(), subject);
        
        // Envoyer directement selon le canal avec les variables
        sendDirectViaChannel(person, type, channel, subject, body, allVariables);
    }
    
    /**
     * Envoie directement via le canal avec les variables déjà remplacées
     */
    private void sendDirectViaChannel(Person person, NotificationType type, NotificationChannel channel, 
                                      String subject, String body, Map<String, String> variables) {
        switch (channel) {
            case EMAIL -> {
                if (person.getEmail() != null && !person.getEmail().isEmpty()) {
                    try {
                        emailService.sendHtmlEmail(person.getEmail(), subject, body);
                        log.info("Email envoyé à: {}", person.getEmail());
                    } catch (Exception e) {
                        log.error("Erreur lors de l'envoi de l'email: {}", e.getMessage());
                    }
                }
            }
            case SMS -> {
                // TODO: Implémenter l'envoi par SMS
                log.warn("SMS non implémenté pour: {}", person.getPhone());
            }
            case PUSH -> {
                // Sauvegarder la notification en base pour l'historique
                Notification notification = new Notification();
                notification.setPerson(person);
                notification.setType(type);
                notification.setTitle(subject);
                notification.setBody(body);
                notification.setChannel(channel);
                notification.setReadFlag(false);
                notificationRepository.save(notification);
                
                sendPushNotification(notification);
            }
            case WHATSAPP -> {
                // TODO: Implémenter WhatsApp
                log.warn("WhatsApp non implémenté pour: {}", person.getPhone());
            }
        }
    }
    
    /**
     * Remplace les variables {{variable}} dans un template
     */
    private String replaceVariables(String template, Map<String, String> variables) {
        if (template == null || variables == null) {
            return template;
        }
        
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            result = result.replace(placeholder, entry.getValue() != null ? entry.getValue() : "");
        }
        return result;
    }

    @Override
    public void sendBulkNotification(List<String> personIds, NotificationType type, 
                                    String title, String message, NotificationChannel channel) {
        for (String personId : personIds) {
            sendNotification(personId, type, title, message, channel);
        }
    }

    @Override
    public List<NotificationResponse> getNotificationsByPerson(String personId) {
        return notificationRepository.findByPersonIdOrderByCreatedAtDesc(personId)
                .stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(String personId) {
        return notificationRepository.findByPersonIdAndReadFlagOrderByCreatedAtDesc(personId, false)
                .stream()
                .map(NotificationResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(String notificationId, String personId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getPerson().getId().equals(personId)) {
            throw new ResourceNotFoundException("Notification", "id", notificationId);
        }

        notification.setReadFlag(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(String personId) {
        List<Notification> notifications = notificationRepository
                .findByPersonIdAndReadFlagOrderByCreatedAtDesc(personId, false);

        for (Notification notification : notifications) {
            notification.setReadFlag(true);
        }

        notificationRepository.saveAll(notifications);
    }

    private void sendViaChannel(Notification notification, NotificationChannel channel) {
        Person person = notification.getPerson();
        
        switch (channel) {
            case SMS -> {
                // TODO: Implémenter l'envoi par SMS via Twilio
                log.warn("SMS non implémenté pour: {}", person.getPhone());
            }
            case EMAIL -> {
                sendEmailNotification(notification);
            }
            case PUSH -> {
                sendPushNotification(notification);
            }
            case WHATSAPP -> {
                // TODO: Implémenter l'envoi via WhatsApp
                log.warn("WhatsApp non implémenté pour: {}", person.getPhone());
            }
        }
    }
    
    /**
     * Envoyer une notification par email
     */
    private void sendEmailNotification(Notification notification) {
        try {
            Person person = notification.getPerson();
            if (person.getEmail() == null || person.getEmail().isEmpty()) {
                log.warn("Pas d'email pour la personne: {}", person.getId());
                return;
            }
            
            // Chercher un template
            var template = templateRepository.findByTypeAndChannelAndLanguageAndActiveTrue(
                notification.getType(), 
                NotificationChannel.EMAIL, 
                "fr"
            );
            
            if (template.isPresent()) {
                // Utiliser le template
                Map<String, String> variables = new HashMap<>();
                variables.put("nom", person.getNom());
                variables.put("prenom", person.getPrenom());
                variables.put("titre", notification.getTitle());
                variables.put("message", notification.getBody());
                
                emailService.sendTemplateEmail(
                    person.getEmail(),
                    template.get().getSubject(),
                    template.get().getBodyTemplate(),
                    variables
                );
            } else {
                // Email simple sans template
                emailService.sendHtmlEmail(
                    person.getEmail(),
                    notification.getTitle(),
                    notification.getBody()
                );
            }
            
            log.info("Email envoyé à: {}", person.getEmail());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email: {}", e.getMessage());
        }
    }
    
    /**
     * Envoyer une notification push
     */
    private void sendPushNotification(Notification notification) {
        try {
            Person person = notification.getPerson();
            
            // Vérifier si la personne a un token FCM
            String fcmToken = person.getFcmToken();
            
            if (fcmToken == null || fcmToken.isEmpty()) {
                log.warn("Pas de token FCM pour la personne: {}", person.getId());
                return;
            }
            
            // Préparer les données
            Map<String, String> data = new HashMap<>();
            data.put("notificationId", notification.getId());
            data.put("type", notification.getType().name());
            
            // Envoyer la notification push
            pushService.sendToDevice(
                fcmToken,
                notification.getTitle(),
                notification.getBody(),
                data
            );
            
            log.info("Push envoyé à la personne: {}", person.getId());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du push: {}", e.getMessage());
        }
    }
     @Override
    public void saveFcmToken(String personId, String fcmToken) {
        Person person = personRepository.findById(personId)
            .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));
        person.setFcmToken(fcmToken);
        personRepository.save(person);
        log.info("Token FCM enregistré pour la personne: {}", personId);
    }

    @Override
    public void deleteNotification(String notificationId, String personId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        
        // Vérifier que la notification appartient bien à la personne
        if (!notification.getPerson().getId().equals(personId)) {
            throw new IllegalArgumentException("Cette notification ne vous appartient pas");
        }
        
        notificationRepository.delete(notification);
        log.info("Notification {} supprimée pour la personne: {}", notificationId, personId);
    }

    @Override
    public void deleteAllNotifications(String personId) {
        List<Notification> notifications = notificationRepository.findByPersonIdOrderByCreatedAtDesc(personId);
        notificationRepository.deleteAll(notifications);
        log.info("Toutes les notifications supprimées pour la personne: {}", personId);
    }

}

   