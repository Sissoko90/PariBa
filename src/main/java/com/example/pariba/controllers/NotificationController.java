package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.NotificationResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.pariba.services.INotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "Gestion des notifications (Push, SMS, Email)")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final INotificationService notificationService;

    public NotificationController(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Mes notifications", description = "Récupère toutes mes notifications")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des notifications")
    })
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        String personId = userDetails.getUsername();
        List<NotificationResponse> notifications = notificationService.getNotificationsByPerson(personId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, notifications));
    }

    @GetMapping("/unread")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Notifications non lues", description = "Récupère uniquement les notifications non lues")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des notifications non lues")
    })
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getUnreadNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        String personId = userDetails.getUsername();
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(personId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, notifications));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Marquer comme lue", description = "Marque une notification comme lue")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notification marquée comme lue")
    })
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        String personId = userDetails.getUsername();
        notificationService.markAsRead(id, personId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.NOTIFICATION_SUCCESS_READ, null));
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Tout marquer comme lu", description = "Marque toutes les notifications comme lues")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Toutes les notifications marquées comme lues")
    })
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        String personId = userDetails.getUsername();
        notificationService.markAllAsRead(personId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.NOTIFICATION_SUCCESS_READ, null));
    }
}
