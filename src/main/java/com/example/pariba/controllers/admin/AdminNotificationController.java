package com.example.pariba.controllers.admin;

import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.models.GroupMembership;
import com.example.pariba.models.Person;
import com.example.pariba.models.TontineGroup;
import com.example.pariba.repositories.GroupMembershipRepository;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.repositories.TontineGroupRepository;
import com.example.pariba.services.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur API pour l'envoi de notifications depuis le dashboard admin
 */
@RestController
@RequestMapping("/admin/api")
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationController {

    private final INotificationService notificationService;
    private final PersonRepository personRepository;
    private final TontineGroupRepository tontineGroupRepository;
    private final GroupMembershipRepository groupMembershipRepository;

    /**
     * DTO pour la requête d'envoi de notification
     */
    public static class SendNotificationRequest {
        private String recipientType; // "user", "group", "all"
        private String userId;
        private String groupId;
        private String title;
        private String message;
        private String channel;

        // Getters et setters
        public String getRecipientType() { return recipientType; }
        public void setRecipientType(String recipientType) { this.recipientType = recipientType; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        
        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getChannel() { return channel; }
        public void setChannel(String channel) { this.channel = channel; }
    }

    /**
     * Envoyer une notification depuis le dashboard admin
     */
    @PostMapping("/send-notification")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Map<String, Object>> sendNotification(@RequestBody SendNotificationRequest request) {
        log.info("📧 Envoi de notification - Type: {}, Canal: {}", request.getRecipientType(), request.getChannel());
        
        try {
            NotificationChannel channel = NotificationChannel.valueOf(request.getChannel());
            NotificationType type = NotificationType.SYSTEM_UPDATE;
            
            int recipientCount = 0;
            
            switch (request.getRecipientType()) {
                case "user":
                    // Envoyer à un utilisateur unique
                    if (request.getUserId() == null || request.getUserId().isEmpty()) {
                        return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "ID utilisateur requis"
                        ));
                    }
                    
                    Person user = personRepository.findById(request.getUserId())
                            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
                    
                    notificationService.sendNotification(
                        request.getUserId(),
                        type,
                        request.getTitle(),
                        request.getMessage(),
                        channel
                    );
                    recipientCount = 1;
                    log.info("✅ Notification envoyée à l'utilisateur: {} {}", user.getPrenom(), user.getNom());
                    break;
                    
                case "group":
                    // Envoyer à tous les membres d'un groupe
                    if (request.getGroupId() == null || request.getGroupId().isEmpty()) {
                        return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "ID groupe requis"
                        ));
                    }
                    
                    TontineGroup group = tontineGroupRepository.findById(request.getGroupId())
                            .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));
                    
                    List<GroupMembership> memberships = groupMembershipRepository.findByGroupId(request.getGroupId());
                    List<String> memberIds = memberships.stream()
                            .map(m -> m.getPerson().getId())
                            .collect(Collectors.toList());
                    
                    if (!memberIds.isEmpty()) {
                        notificationService.sendBulkNotification(
                            memberIds,
                            type,
                            request.getTitle(),
                            request.getMessage(),
                            channel
                        );
                        recipientCount = memberIds.size();
                    }
                    log.info("✅ Notification envoyée à {} membres du groupe: {}", recipientCount, group.getNom());
                    break;
                    
                case "all":
                    // Envoyer à tous les utilisateurs
                    List<Person> allUsers = personRepository.findAll();
                    List<String> allUserIds = allUsers.stream()
                            .map(Person::getId)
                            .collect(Collectors.toList());
                    
                    if (!allUserIds.isEmpty()) {
                        notificationService.sendBulkNotification(
                            allUserIds,
                            type,
                            request.getTitle(),
                            request.getMessage(),
                            channel
                        );
                        recipientCount = allUserIds.size();
                    }
                    log.info("✅ Notification envoyée à tous les utilisateurs: {} destinataires", recipientCount);
                    break;
                    
                default:
                    return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Type de destinataire invalide"
                    ));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notification envoyée avec succès");
            response.put("recipientCount", recipientCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de la notification", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Erreur lors de l'envoi: " + e.getMessage()
            ));
        }
    }
}
