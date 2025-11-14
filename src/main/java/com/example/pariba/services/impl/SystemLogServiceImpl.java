package com.example.pariba.services.impl;

import com.example.pariba.models.SystemLog;
import com.example.pariba.repositories.SystemLogRepository;
import com.example.pariba.services.ISystemLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service de logs système
 */
@Service
@Slf4j
public class SystemLogServiceImpl implements ISystemLogService {
    
    private final SystemLogRepository systemLogRepository;
    
    public SystemLogServiceImpl(SystemLogRepository systemLogRepository) {
        this.systemLogRepository = systemLogRepository;
    }
    
    @Override
    @Async
    public void log(String userId, String userName, String action, String entityType, String entityId, String details, String level, boolean success) {
        try {
            SystemLog systemLog = new SystemLog();
            systemLog.setUserId(userId != null ? userId : "SYSTEM");
            systemLog.setUserName(userName != null ? userName : "System");
            systemLog.setAction(action);
            systemLog.setEntityType(entityType);
            systemLog.setEntityId(entityId);
            systemLog.setDetails(details);
            systemLog.setLevel(level);
            systemLog.setSuccess(success);
            
            systemLogRepository.save(systemLog);
        } catch (Exception e) {
            log.error("Erreur lors du logging: {}", e.getMessage());
        }
    }
    
    @Override
    @Async
    public void log(String userId, String userName, String action, String entityType, String entityId, String details, HttpServletRequest request) {
        try {
            SystemLog systemLog = new SystemLog();
            systemLog.setUserId(userId != null ? userId : "SYSTEM");
            systemLog.setUserName(userName != null ? userName : "System");
            systemLog.setAction(action);
            systemLog.setEntityType(entityType);
            systemLog.setEntityId(entityId);
            systemLog.setDetails(details);
            systemLog.setLevel("INFO");
            systemLog.setSuccess(true);
            
            if (request != null) {
                systemLog.setIpAddress(getClientIp(request));
                systemLog.setUserAgent(request.getHeader("User-Agent"));
            }
            
            systemLogRepository.save(systemLog);
        } catch (Exception e) {
            log.error("Erreur lors du logging: {}", e.getMessage());
        }
    }
    
    @Override
    @Async
    public void logInfo(String userId, String userName, String action) {
        log(userId, userName, action, null, null, null, "INFO", true);
    }
    
    @Override
    @Async
    public void logError(String userId, String userName, String action, String details) {
        log(userId, userName, action, null, null, details, "ERROR", false);
    }
    
    /**
     * Récupérer l'IP du client
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
