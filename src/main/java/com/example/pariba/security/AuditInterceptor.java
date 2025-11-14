package com.example.pariba.security;

import com.example.pariba.utils.IpAddressUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Intercepteur pour capturer l'adresse IP et la stocker dans le contexte de la requête
 */
@Component
public class AuditInterceptor implements HandlerInterceptor {
    
    public static final String CLIENT_IP_ATTRIBUTE = "clientIpAddress";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Extraire et stocker l'IP dans les attributs de la requête
        String clientIp = IpAddressUtil.getClientIpAddress(request);
        request.setAttribute(CLIENT_IP_ATTRIBUTE, clientIp);
        return true;
    }
}
