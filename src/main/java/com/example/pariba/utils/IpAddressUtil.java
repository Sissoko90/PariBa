package com.example.pariba.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utilitaire pour extraire l'adresse IP réelle du client
 */
public class IpAddressUtil {
    
    /**
     * Extrait l'adresse IP réelle du client en tenant compte des proxies et load balancers
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "Unknown";
        }
        
        // Vérifier les headers de proxy dans l'ordre de priorité
        String[] headers = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For peut contenir plusieurs IPs séparées par des virgules
                // La première est l'IP du client original
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        // Fallback sur l'IP de la connexion directe
        String remoteAddr = request.getRemoteAddr();
        if (remoteAddr != null && !remoteAddr.isEmpty()) {
            // Normaliser l'adresse IPv6 localhost en IPv4
            if ("0:0:0:0:0:0:0:1".equals(remoteAddr) || "::1".equals(remoteAddr)) {
                return "127.0.0.1";
            }
            return remoteAddr;
        }
        return "Unknown";
    }
}
