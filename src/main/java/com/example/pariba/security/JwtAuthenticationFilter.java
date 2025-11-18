package com.example.pariba.security;

import com.example.pariba.constants.AppConstants;
import com.example.pariba.services.IJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(IJwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String header = request.getHeader(AppConstants.JWT_HEADER);
        
        if (header != null && header.startsWith(AppConstants.JWT_TOKEN_PREFIX)) {
            String token = header.substring(AppConstants.JWT_TOKEN_PREFIX.length());
            
            if (jwtService.validateToken(token)) {
                String personId = jwtService.getPersonIdFromToken(token);
                
                try {
                    // Charger les authorities depuis la base de données en utilisant l'ID de la personne
                    UserDetails userDetails = userDetailsService.loadUserByPersonId(personId);
                    
                    log.debug("JWT Filter - User: {}, Authorities: {}", personId, userDetails.getAuthorities());
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(personId, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    log.error("Erreur lors du chargement des authorities pour: {}", personId, e);
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Ne pas appliquer le filtre JWT sur les URLs du dashboard admin
     * Le dashboard utilise l'authentification par formulaire
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/admin/") || 
               path.startsWith("/css/") || 
               path.startsWith("/js/") || 
               path.startsWith("/images/");
    }
}
