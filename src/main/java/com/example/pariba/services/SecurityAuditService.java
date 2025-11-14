package com.example.pariba.services;

import com.example.pariba.models.AuditLog;
import com.example.pariba.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service d'audit de sécurité
 * Enregistre toutes les actions sensibles pour traçabilité
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityAuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Enregistre un événement de sécurité de manière asynchrone
     */
    @Async
    public void logSecurityEvent(String eventType, String username, String ipAddress, String details) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setEventType(eventType);
            auditLog.setUsername(username);
            auditLog.setIpAddress(ipAddress);
            auditLog.setDetails(details);
            auditLog.setTimestamp(Instant.now());
            
            auditLogRepository.save(auditLog);
            
            log.info("Security event logged: {} for user: {}", eventType, username);
        } catch (Exception e) {
            log.error("Failed to log security event", e);
        }
    }

    /**
     * Log tentative de connexion échouée
     */
    public void logFailedLogin(String username, String ipAddress) {
        logSecurityEvent("FAILED_LOGIN", username, ipAddress, 
                        "Tentative de connexion échouée");
    }

    /**
     * Log connexion réussie
     */
    public void logSuccessfulLogin(String username, String ipAddress) {
        logSecurityEvent("SUCCESSFUL_LOGIN", username, ipAddress, 
                        "Connexion réussie");
    }

    /**
     * Log tentative d'accès non autorisé
     */
    public void logUnauthorizedAccess(String username, String resource, String ipAddress) {
        logSecurityEvent("UNAUTHORIZED_ACCESS", username, ipAddress, 
                        "Tentative d'accès non autorisé à: " + resource);
    }

    /**
     * Log modification de données sensibles
     */
    public void logDataModification(String username, String entityType, String entityId, String action) {
        logSecurityEvent("DATA_MODIFICATION", username, null, 
                        String.format("Action: %s sur %s (ID: %s)", action, entityType, entityId));
    }

    /**
     * Log création d'utilisateur
     */
    public void logUserCreation(String adminUsername, String newUserPhone) {
        logSecurityEvent("USER_CREATED", adminUsername, null, 
                        "Nouvel utilisateur créé: " + newUserPhone);
    }

    /**
     * Log suppression d'utilisateur
     */
    public void logUserDeletion(String adminUsername, String deletedUserPhone) {
        logSecurityEvent("USER_DELETED", adminUsername, null, 
                        "Utilisateur supprimé: " + deletedUserPhone);
    }

    /**
     * Log changement de rôle
     */
    public void logRoleChange(String adminUsername, String targetUser, String oldRole, String newRole) {
        logSecurityEvent("ROLE_CHANGED", adminUsername, null, 
                        String.format("Rôle de %s changé de %s à %s", targetUser, oldRole, newRole));
    }

    /**
     * Log accès au dashboard admin
     */
    public void logAdminDashboardAccess(String username, String ipAddress) {
        logSecurityEvent("ADMIN_DASHBOARD_ACCESS", username, ipAddress, 
                        "Accès au dashboard administrateur");
    }

    /**
     * Log export de données
     */
    public void logDataExport(String username, String exportType) {
        logSecurityEvent("DATA_EXPORT", username, null, 
                        "Export de données: " + exportType);
    }

    /**
     * Log changement de configuration système
     */
    public void logSystemConfigChange(String adminUsername, String configKey, String oldValue, String newValue) {
        logSecurityEvent("SYSTEM_CONFIG_CHANGE", adminUsername, null, 
                        String.format("Configuration %s changée de '%s' à '%s'", configKey, oldValue, newValue));
    }
}
