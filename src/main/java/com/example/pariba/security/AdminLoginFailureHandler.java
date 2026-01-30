package com.example.pariba.security;

import com.example.pariba.services.SecurityAuditService;
import com.example.pariba.utils.IpAddressUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler pour les échecs de connexion au dashboard admin
 * Enregistre l'événement dans les logs d'audit et limite les tentatives
 */
@Slf4j
@Component
public class AdminLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    
    private final SecurityAuditService securityAuditService;
    
    // Stockage des tentatives de connexion (username -> LoginAttempt)
    private final Map<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();
    
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 15;
    
    public AdminLoginFailureHandler(SecurityAuditService securityAuditService) {
        super("/admin/login?error");
        this.securityAuditService = securityAuditService;
    }
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                       HttpServletResponse response,
                                       AuthenticationException exception) throws IOException, ServletException {
        
        String username = request.getParameter("username");
        String ipAddress = IpAddressUtil.getClientIpAddress(request);
        
        String redirectUrl = "/admin/login?error";
        
        if (username != null) {
            LoginAttempt attempt = loginAttempts.computeIfAbsent(username, k -> new LoginAttempt());
            
            // Vérifier si le compte est bloqué
            if (attempt.isLocked()) {
                long minutesRemaining = attempt.getMinutesUntilUnlock();
                log.warn("🔒 Compte bloqué pour: {} - Reste {} minutes", username, minutesRemaining);
                redirectUrl = "/admin/login?error=locked&minutes=" + minutesRemaining;
            } else {
                // Incrémenter les tentatives
                attempt.incrementAttempts();
                
                if (attempt.getAttempts() >= MAX_ATTEMPTS) {
                    attempt.lock();
                    log.warn("🔒 Compte bloqué pour {} tentatives échouées: {}", MAX_ATTEMPTS, username);
                    redirectUrl = "/admin/login?error=locked&minutes=" + LOCK_DURATION_MINUTES;
                } else {
                    int remainingAttempts = MAX_ATTEMPTS - attempt.getAttempts();
                    log.warn("❌ Échec de connexion pour: {} ({} tentatives restantes)", username, remainingAttempts);
                    redirectUrl = "/admin/login?error=invalid&remaining=" + remainingAttempts;
                }
            }
        }
        
        // Logger la tentative échouée
        securityAuditService.logFailedLogin(username != null ? username : "unknown", ipAddress);
        log.warn("❌ Échec de connexion pour: {} depuis {} - Raison: {}", 
                 username, ipAddress, exception.getMessage());
        
        // Définir l'URL de redirection et rediriger
        setDefaultFailureUrl(redirectUrl);
        super.onAuthenticationFailure(request, response, exception);
    }
    
    /**
     * Réinitialiser les tentatives après une connexion réussie
     */
    public void resetAttempts(String username) {
        loginAttempts.remove(username);
        log.info("✅ Tentatives de connexion réinitialisées pour: {}", username);
    }
    
    /**
     * Classe interne pour gérer les tentatives de connexion
     */
    private static class LoginAttempt {
        private int attempts = 0;
        private Instant lockTime = null;
        
        public void incrementAttempts() {
            attempts++;
        }
        
        public int getAttempts() {
            return attempts;
        }
        
        public void lock() {
            lockTime = Instant.now();
        }
        
        public boolean isLocked() {
            if (lockTime == null) {
                return false;
            }
            
            Instant unlockTime = lockTime.plusSeconds(LOCK_DURATION_MINUTES * 60);
            if (Instant.now().isAfter(unlockTime)) {
                // Débloquer automatiquement après la durée
                lockTime = null;
                attempts = 0;
                return false;
            }
            
            return true;
        }
        
        public long getMinutesUntilUnlock() {
            if (lockTime == null) {
                return 0;
            }
            
            Instant unlockTime = lockTime.plusSeconds(LOCK_DURATION_MINUTES * 60);
            long secondsRemaining = unlockTime.getEpochSecond() - Instant.now().getEpochSecond();
            return Math.max(0, (secondsRemaining + 59) / 60); // Arrondir au supérieur
        }
    }
}
