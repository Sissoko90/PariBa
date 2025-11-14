package com.example.pariba.services.impl;

import com.example.pariba.dtos.responses.AuditLogResponse;
import com.example.pariba.models.AuditLog;
import com.example.pariba.models.Person;
import com.example.pariba.repositories.AuditLogRepository;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.security.AuditInterceptor;
import com.example.pariba.services.IAuditService;
import com.example.pariba.utils.IpAddressUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditServiceImpl implements IAuditService {

    private final AuditLogRepository auditLogRepository;
    private final PersonRepository personRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository, PersonRepository personRepository) {
        this.auditLogRepository = auditLogRepository;
        this.personRepository = personRepository;
    }

    @Transactional
    public void log(String actorId, String action, String entityType, String entityId, String detailsJson) {
        // Récupérer automatiquement l'IP depuis le contexte de la requête
        String ipAddress = getCurrentRequestIpAddress();
        log(actorId, action, entityType, entityId, detailsJson, ipAddress);
    }
    
    @Transactional
    public void log(String actorId, String action, String entityType, String entityId, String detailsJson, String ipAddress) {
        Person actor = personRepository.findById(actorId).orElse(null);

        AuditLog auditLog = new AuditLog();
        auditLog.setActor(actor);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDetailsJson(detailsJson);
        auditLog.setIpAddress(ipAddress);
        auditLog.setTimestamp(Instant.now());
        
        // Si l'acteur existe, utiliser son username
        if (actor != null) {
            auditLog.setUsername(actor.getPhone());
        }
        
        auditLogRepository.save(auditLog);
    }
    
    /**
     * Récupère l'adresse IP de la requête HTTP courante
     */
    private String getCurrentRequestIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                // Récupérer l'IP depuis l'attribut stocké par l'intercepteur
                String ip = (String) request.getAttribute(AuditInterceptor.CLIENT_IP_ATTRIBUTE);
                if (ip != null) {
                    return ip;
                }
                // Fallback: extraire directement depuis la requête
                return IpAddressUtil.getClientIpAddress(request);
            }
        } catch (Exception e) {
            // Si on n'est pas dans un contexte de requête HTTP (ex: tâche async)
            return "System";
        }
        return "Unknown";
    }

    public List<AuditLogResponse> getLogsByEntity(String entityType, String entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId)
                .stream()
                .map(AuditLogResponse::new)
                .collect(Collectors.toList());
    }

    public List<AuditLogResponse> getLogsByActor(String actorId) {
        return auditLogRepository.findByActorIdOrderByCreatedAtDesc(actorId)
                .stream()
                .map(AuditLogResponse::new)
                .collect(Collectors.toList());
    }

    public List<AuditLogResponse> getLogsByDateRange(Instant start, Instant end) {
        return auditLogRepository.findByDateRange(start, end)
                .stream()
                .map(AuditLogResponse::new)
                .collect(Collectors.toList());
    }
}
