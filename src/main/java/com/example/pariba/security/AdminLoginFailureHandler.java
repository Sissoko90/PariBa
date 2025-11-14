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

/**
 * Handler pour les échecs de connexion au dashboard admin
 * Enregistre l'événement dans les logs d'audit
 */
@Slf4j
@Component
public class AdminLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    
    private final SecurityAuditService securityAuditService;
    
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
        
        // Logger la tentative échouée
        securityAuditService.logFailedLogin(username != null ? username : "unknown", ipAddress);
        log.warn("❌ Échec de connexion pour: {} depuis {} - Raison: {}", 
                 username, ipAddress, exception.getMessage());
        
        // Rediriger vers la page de login avec erreur
        super.onAuthenticationFailure(request, response, exception);
    }
}
