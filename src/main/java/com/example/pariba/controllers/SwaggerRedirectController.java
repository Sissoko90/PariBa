package com.example.pariba.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur pour rediriger vers Swagger avec authentification
 */
@Controller
public class SwaggerRedirectController {
    
    /**
     * Redirection vers Swagger UI - Nécessite SUPERADMIN
     */
    @GetMapping("/swagger")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public String swagger() {
        return "redirect:/swagger-ui/index.html";
    }
}
