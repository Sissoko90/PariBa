package com.example.pariba.controllers;

import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.models.NotificationPreference;
import com.example.pariba.models.Person;
import com.example.pariba.repositories.NotificationPreferenceRepository;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour gérer les préférences de notifications
 */
@RestController
@RequestMapping("/notification-preferences")
@Tag(name = "Préférences Notifications", description = "Gestion des préférences de notifications")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class NotificationPreferenceController {
    
    private final NotificationPreferenceRepository preferenceRepository;
    private final PersonRepository personRepository;
    private final CurrentUser currentUser;
    
    public NotificationPreferenceController(NotificationPreferenceRepository preferenceRepository,
                                           PersonRepository personRepository,
                                           CurrentUser currentUser) {
        this.preferenceRepository = preferenceRepository;
        this.personRepository = personRepository;
        this.currentUser = currentUser;
    }
    
    @GetMapping("/my-preferences")
    @Operation(summary = "Mes préférences", description = "Récupère toutes mes préférences de notifications")
    public ResponseEntity<ApiResponse<List<NotificationPreference>>> getMyPreferences() {
        String personId = currentUser.getPersonId();
        List<NotificationPreference> preferences = preferenceRepository.findByPersonId(personId);
        return ResponseEntity.ok(ApiResponse.success("Préférences récupérées", preferences));
    }
    
    @PostMapping("/toggle")
    @Operation(summary = "Activer/Désactiver", description = "Active ou désactive une notification pour un canal")
    public ResponseEntity<ApiResponse<NotificationPreference>> togglePreference(
            @RequestBody Map<String, String> request) {
        
        String personId = currentUser.getPersonId();
        NotificationType type = NotificationType.valueOf(request.get("type"));
        NotificationChannel channel = NotificationChannel.valueOf(request.get("channel"));
        boolean enabled = Boolean.parseBoolean(request.getOrDefault("enabled", "true"));
        
        Person person = personRepository.findById(personId).orElseThrow();
        
        NotificationPreference preference = preferenceRepository
            .findByPersonIdAndNotificationTypeAndChannel(personId, type, channel)
            .orElse(new NotificationPreference());
        
        preference.setPerson(person);
        preference.setNotificationType(type);
        preference.setChannel(channel);
        preference.setEnabled(enabled);
        
        preference = preferenceRepository.save(preference);
        log.info("Préférence mise à jour: {} - {} - {}", type, channel, enabled);
        
        return ResponseEntity.ok(ApiResponse.success("Préférence mise à jour", preference));
    }
    
    @PostMapping("/disable-all")
    @Operation(summary = "Tout désactiver", description = "Désactive toutes les notifications")
    public ResponseEntity<ApiResponse<String>> disableAll() {
        String personId = currentUser.getPersonId();
        List<NotificationPreference> preferences = preferenceRepository.findByPersonId(personId);
        
        preferences.forEach(pref -> pref.setEnabled(false));
        preferenceRepository.saveAll(preferences);
        
        log.info("Toutes les notifications désactivées pour: {}", personId);
        return ResponseEntity.ok(ApiResponse.success("Toutes les notifications désactivées", null));
    }
    
    @PostMapping("/enable-all")
    @Operation(summary = "Tout activer", description = "Active toutes les notifications")
    public ResponseEntity<ApiResponse<String>> enableAll() {
        String personId = currentUser.getPersonId();
        List<NotificationPreference> preferences = preferenceRepository.findByPersonId(personId);
        
        preferences.forEach(pref -> pref.setEnabled(true));
        preferenceRepository.saveAll(preferences);
        
        log.info("Toutes les notifications activées pour: {}", personId);
        return ResponseEntity.ok(ApiResponse.success("Toutes les notifications activées", null));
    }
}
