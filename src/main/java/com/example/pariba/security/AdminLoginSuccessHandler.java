package com.example.pariba.security;

import com.example.pariba.models.Person;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.services.IRefreshTokenService;
import com.example.pariba.services.SecurityAuditService;
import com.example.pariba.utils.IpAddressUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handler pour les connexions réussies au dashboard admin
 * Enregistre l'événement dans les logs d'audit et génère un refresh token
 */
@Slf4j
@Component
public class AdminLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final SecurityAuditService securityAuditService;
    private final IRefreshTokenService refreshTokenService;
    private final PersonRepository personRepository;
    private final AdminLoginFailureHandler loginFailureHandler;
    
    public AdminLoginSuccessHandler(SecurityAuditService securityAuditService,
                                   IRefreshTokenService refreshTokenService,
                                   PersonRepository personRepository,
                                   AdminLoginFailureHandler loginFailureHandler) {
        super();
        this.securityAuditService = securityAuditService;
        this.refreshTokenService = refreshTokenService;
        this.personRepository = personRepository;
        this.loginFailureHandler = loginFailureHandler;
        setDefaultTargetUrl("/admin/dashboard");
        setAlwaysUseDefaultTargetUrl(true);
    }
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                       HttpServletResponse response,
                                       Authentication authentication) throws ServletException, IOException {
        
        String username = authentication.getName();
        String ipAddress = IpAddressUtil.getClientIpAddress(request);
        
        // Réinitialiser les tentatives de connexion échouées
        loginFailureHandler.resetAttempts(username);
        
        // Logger la connexion réussie
        securityAuditService.logSuccessfulLogin(username, ipAddress);
        log.info("✅ Connexion réussie pour: {} depuis {}", username, ipAddress);
        
        // Générer un refresh token pour l'admin
        try {
            // Chercher la personne par email ou téléphone
            Person person = personRepository.findByEmail(username)
                .orElseGet(() -> personRepository.findByPhone(username).orElse(null));
            
            if (person != null && person.getUser() != null) {
                com.example.pariba.models.RefreshToken refreshTokenEntity = 
                    refreshTokenService.createRefreshToken(person.getUser(), ipAddress, request.getHeader("User-Agent"));
                
                // Stocker le refresh token dans un cookie HTTP-only sécurisé
                Cookie refreshTokenCookie = new Cookie("admin_refresh_token", refreshTokenEntity.getToken());
                refreshTokenCookie.setHttpOnly(true);
                refreshTokenCookie.setSecure(true); // HTTPS obligatoire
                refreshTokenCookie.setPath("/");
                refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 jours
                refreshTokenCookie.setAttribute("SameSite", "Strict"); // Protection CSRF
                response.addCookie(refreshTokenCookie);
                
                log.info("🔑 Refresh token généré et stocké pour l'admin: {}", username);
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la génération du refresh token pour l'admin", e);
        }
        
        // Rediriger toujours vers le dashboard
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
