package com.example.pariba.services;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service pour logger les activités système
 */
public interface ISystemLogService {
    
    /**
     * Logger une action
     */
    void log(String userId, String userName, String action, String entityType, String entityId, String details, String level, boolean success);
    
    /**
     * Logger une action avec request HTTP
     */
    void log(String userId, String userName, String action, String entityType, String entityId, String details, HttpServletRequest request);
    
    /**
     * Logger une action simple
     */
    void logInfo(String userId, String userName, String action);
    
    /**
     * Logger une erreur
     */
    void logError(String userId, String userName, String action, String details);
}
