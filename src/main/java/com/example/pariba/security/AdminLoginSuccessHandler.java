package com.example.pariba.security;

import com.example.pariba.services.SecurityAuditService;
import com.example.pariba.utils.IpAddressUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler pour les connexions réussies au dashboard admin
 * Enregistre l'événement dans les logs d'audit
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    
    private final SecurityAuditService securityAuditService;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                       HttpServletResponse response,
                                       Authentication authentication) throws ServletException, IOException {
        
        String username = authentication.getName();
        String ipAddress = IpAddressUtil.getClientIpAddress(request);
        
        // Logger la connexion réussie
        securityAuditService.logSuccessfulLogin(username, ipAddress);
        log.info("✅ Connexion réussie pour: {} depuis {}", username, ipAddress);
        
        // Définir l'URL de redirection par défaut
        setDefaultTargetUrl("/admin/dashboard");
        
        // Rediriger vers la page demandée ou le dashboard
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
