package com.example.pariba.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtre de limitation de débit (Rate Limiting)
 * Protège contre les attaques par force brute et le spam
 */
@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    // Configuration: 100 requêtes par minute par IP
    private static final int CAPACITY = 100;
    private static final Duration REFILL_DURATION = Duration.ofMinutes(1);
    
    // Configuration stricte pour les endpoints sensibles
    private static final int LOGIN_CAPACITY = 20;  // 20 tentatives (augmenté pour debug)
    private static final Duration LOGIN_REFILL = Duration.ofMinutes(1);

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String key = getClientIP(request);
        String path = request.getRequestURI();
        
        // Appliquer rate limiting strict sur les endpoints de login
        boolean isLoginEndpoint = path.contains("/login") || path.contains("/auth/login");
        
        Bucket bucket = resolveBucket(key, isLoginEndpoint);
        
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for IP: {} on path: {}", key, path);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"success\":false,\"message\":\"Trop de requêtes. Veuillez réessayer dans quelques instants.\"}"
            );
        }
    }

    /**
     * Résout ou crée un bucket pour une clé donnée
     */
    private Bucket resolveBucket(String key, boolean isLoginEndpoint) {
        return cache.computeIfAbsent(key, k -> createNewBucket(isLoginEndpoint));
    }

    /**
     * Crée un nouveau bucket avec les limites appropriées
     */
    private Bucket createNewBucket(boolean isLoginEndpoint) {
        Bandwidth limit;
        
        if (isLoginEndpoint) {
            // Limite stricte pour login: 5 tentatives par minute
            limit = Bandwidth.builder()
                    .capacity(LOGIN_CAPACITY)
                    .refillIntervally(LOGIN_CAPACITY, LOGIN_REFILL)
                    .build();
        } else {
            // Limite normale: 100 requêtes par minute
            limit = Bandwidth.builder()
                    .capacity(CAPACITY)
                    .refillIntervally(CAPACITY, REFILL_DURATION)
                    .build();
        }
        
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Extrait l'adresse IP du client (supporte les proxies)
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        // Prendre la première IP si plusieurs (proxy chain)
        return xfHeader.split(",")[0].trim();
    }
    
    /**
     * Ne pas appliquer le rate limiting sur les ressources statiques
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/css/") || 
               path.startsWith("/js/") || 
               path.startsWith("/images/") ||
               path.startsWith("/webjars/") ||
               path.startsWith("/favicon.ico");
    }
}
