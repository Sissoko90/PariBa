package com.example.pariba.controllers.admin;

import com.example.pariba.repositories.TontineGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrôleur Thymeleaf pour la validation des paiements cash
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminCashPaymentViewController {
    
    private final TontineGroupRepository tontineGroupRepository;
    
    @GetMapping("/cash-payments")
    @PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN')")
    public String cashPayments(Model model, Authentication authentication) {
        log.info("💵 Accès à la validation des paiements cash par: {}", authentication.getName());
        
        try {
            // Récupérer tous les groupes pour les admins
            var groups = tontineGroupRepository.findAll();
            
            model.addAttribute("pageTitle", "Validation Paiements Cash");
            model.addAttribute("groups", groups);
            
            return "admin/cash-payments";
        } catch (Exception e) {
            log.error("Erreur lors du chargement des groupes", e);
            model.addAttribute("error", "Erreur lors du chargement des données");
            return "admin/error";
        }
    }
}
