package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.NotificationResponse;
import com.example.pariba.services.INotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "Gestion des notifications (Push, SMS, Email)")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final INotificationService notificationService;

    public NotificationController(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    private String getPersonIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        
        // Le principal est un UserDetails, pas une String
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            return ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        }
        
        if (principal instanceof String) {
            return (String) principal;
        }
        
        return null;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Mes notifications", description = "Récupère toutes mes notifications")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des notifications")
    })
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications() {
        try {
            String personId = getPersonIdFromAuthentication();
            
            if (personId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, "Utilisateur non authentifié", null)
                );
            }
            
            List<NotificationResponse> notifications = notificationService.getNotificationsByPerson(personId);
            
            return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, notifications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponse<>(false, "Erreur: " + e.getMessage(), null)
            );
        }
    }

    @GetMapping("/unread")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Notifications non lues", description = "Récupère uniquement les notifications non lues")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des notifications non lues")
    })
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications() {
        try {
            String personId = getPersonIdFromAuthentication();
            
            if (personId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, "Utilisateur non authentifié", null)
                );
            }
            
            List<NotificationResponse> notifications = notificationService.getUnreadNotifications(personId);
            
            return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, notifications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponse<>(false, "Erreur: " + e.getMessage(), null)
            );
        }
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Marquer comme lue", description = "Marque une notification comme lue")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notification marquée comme lue")
    })
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable String id) {
        try {
            String personId = getPersonIdFromAuthentication();
            
            if (personId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, "Utilisateur non authentifié", null)
                );
            }
            
            notificationService.markAsRead(id, personId);
            
            return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.NOTIFICATION_SUCCESS_READ, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponse<>(false, "Erreur: " + e.getMessage(), null)
            );
        }
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Tout marquer comme lu", description = "Marque toutes les notifications comme lues")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Toutes les notifications marquées comme lues")
    })
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        try {
            String personId = getPersonIdFromAuthentication();
            
            if (personId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, "Utilisateur non authentifié", null)
                );
            }
            
            notificationService.markAllAsRead(personId);
            
            return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.NOTIFICATION_SUCCESS_READ, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponse<>(false, "Erreur: " + e.getMessage(), null)
            );
        }
    }

    @PostMapping("/fcm-token")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Enregistrer token FCM", description = "Enregistre le token FCM pour les notifications push")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token FCM enregistré avec succès")
    })
    public ResponseEntity<ApiResponse<Void>> registerFcmToken(@RequestBody Map<String, String> request) {
        try {
            String personId = getPersonIdFromAuthentication();
            
            if (personId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, "Utilisateur non authentifié", null)
                );
            }
            
            String fcmToken = request.get("token");
            
            if (fcmToken == null || fcmToken.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Token FCM manquant", null)
                );
            }
            
            notificationService.saveFcmToken(personId, fcmToken);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Token FCM enregistré avec succès", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponse<>(false, "Erreur: " + e.getMessage(), null)
            );
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Supprimer une notification", description = "Supprime une notification spécifique")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notification supprimée avec succès")
    })
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable String id) {
        try {
            String personId = getPersonIdFromAuthentication();
            
            if (personId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, "Utilisateur non authentifié", null)
                );
            }
            
            notificationService.deleteNotification(id, personId);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Notification supprimée avec succès", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponse<>(false, "Erreur: " + e.getMessage(), null)
            );
        }
    }

    @DeleteMapping("/delete-all")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Supprimer toutes les notifications", description = "Supprime toutes les notifications de l'utilisateur")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Toutes les notifications supprimées")
    })
    public ResponseEntity<ApiResponse<Void>> deleteAllNotifications() {
        try {
            String personId = getPersonIdFromAuthentication();
            
            if (personId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, "Utilisateur non authentifié", null)
                );
            }
            
            notificationService.deleteAllNotifications(personId);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Toutes les notifications ont été supprimées", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponse<>(false, "Erreur: " + e.getMessage(), null)
            );
        }
    }
}
