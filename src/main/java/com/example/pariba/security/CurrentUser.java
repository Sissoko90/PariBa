package com.example.pariba.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    /**
     * Récupère l'ID de l'utilisateur actuellement authentifié
     */
    public String getPersonId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            
            // Si le principal est un UserDetails, récupérer le username (qui contient le personId)
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
            
            // Si le principal est une String (ancien comportement)
            if (principal instanceof String) {
                return (String) principal;
            }
        }
        return null;
    }

    /**
     * Vérifie si un utilisateur est authentifié
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}
