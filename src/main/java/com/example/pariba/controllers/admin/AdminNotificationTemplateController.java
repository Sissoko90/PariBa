package com.example.pariba.controllers.admin;

import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.models.NotificationTemplate;
import com.example.pariba.repositories.NotificationTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur admin pour gérer les templates de notifications
 */
@Controller
@RequestMapping("/admin/notification-templates")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
@Slf4j
public class AdminNotificationTemplateController {
    
    private final NotificationTemplateRepository templateRepository;
    
    public AdminNotificationTemplateController(NotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }
    
    /**
     * Liste des templates avec pagination
     */
    @GetMapping
    public String listTemplates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) NotificationChannel channel,
            Model model
    ) {
        Page<NotificationTemplate> templates;
        
        if (type != null && channel != null) {
            templates = templateRepository.findAll(PageRequest.of(page, size, Sort.by("type", "channel")));
        } else if (type != null) {
            templates = templateRepository.findAll(PageRequest.of(page, size, Sort.by("type")));
        } else {
            templates = templateRepository.findAll(PageRequest.of(page, size, Sort.by("type", "channel")));
        }
        
        model.addAttribute("templates", templates.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", templates.getTotalPages());
        model.addAttribute("totalItems", templates.getTotalElements());
        model.addAttribute("pageTitle", "Gestion des Templates de Notifications");
        model.addAttribute("notificationTypes", NotificationType.values());
        model.addAttribute("notificationChannels", NotificationChannel.values());
        
        return "admin/notification-templates";
    }
    
    /**
     * Afficher le formulaire de création
     */
    @GetMapping("/new")
    public String newTemplateForm(Model model) {
        model.addAttribute("template", new NotificationTemplate());
        model.addAttribute("notificationTypes", NotificationType.values());
        model.addAttribute("notificationChannels", NotificationChannel.values());
        model.addAttribute("templateVariables", getTemplateVariables());
        model.addAttribute("pageTitle", "Nouveau Template");
        model.addAttribute("isEdit", false);
        return "admin/notification-template-form";
    }
    
    /**
     * Afficher le formulaire d'édition
     */
    @GetMapping("/edit/{id}")
    public String editTemplateForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        NotificationTemplate template = templateRepository.findById(id).orElse(null);
        
        if (template == null) {
            redirectAttributes.addFlashAttribute("error", "Template non trouvé");
            return "redirect:/admin/notification-templates";
        }
        
        model.addAttribute("template", template);
        model.addAttribute("notificationTypes", NotificationType.values());
        model.addAttribute("notificationChannels", NotificationChannel.values());
        model.addAttribute("templateVariables", getTemplateVariables());
        model.addAttribute("pageTitle", "Modifier Template");
        model.addAttribute("isEdit", true);
        return "admin/notification-template-form";
    }
    
    /**
     * Créer un nouveau template
     */
    @PostMapping
    public String createTemplate(
            @ModelAttribute NotificationTemplate template,
            RedirectAttributes redirectAttributes
    ) {
        try {
            templateRepository.save(template);
            redirectAttributes.addFlashAttribute("success", "Template créé avec succès");
            log.info("Template créé: {} - {}", template.getType(), template.getChannel());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création: " + e.getMessage());
            log.error("Erreur création template: {}", e.getMessage());
        }
        
        return "redirect:/admin/notification-templates";
    }
    
    /**
     * Mettre à jour un template
     */
    @PostMapping("/update/{id}")
    public String updateTemplate(
            @PathVariable String id,
            @ModelAttribute NotificationTemplate template,
            RedirectAttributes redirectAttributes
    ) {
        try {
            NotificationTemplate existing = templateRepository.findById(id).orElse(null);
            
            if (existing == null) {
                redirectAttributes.addFlashAttribute("error", "Template non trouvé");
                return "redirect:/admin/notification-templates";
            }
            
            existing.setType(template.getType());
            existing.setChannel(template.getChannel());
            existing.setSubject(template.getSubject());
            existing.setBodyTemplate(template.getBodyTemplate());
            existing.setActive(template.isActive());
            existing.setLanguage(template.getLanguage());
            
            templateRepository.save(existing);
            redirectAttributes.addFlashAttribute("success", "Template mis à jour avec succès");
            log.info("Template mis à jour: {}", id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour: " + e.getMessage());
            log.error("Erreur mise à jour template: {}", e.getMessage());
        }
        
        return "redirect:/admin/notification-templates";
    }
    
    /**
     * Activer/Désactiver un template
     */
    @PostMapping("/toggle/{id}")
    public String toggleTemplate(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            NotificationTemplate template = templateRepository.findById(id).orElse(null);
            
            if (template == null) {
                redirectAttributes.addFlashAttribute("error", "Template non trouvé");
                return "redirect:/admin/notification-templates";
            }
            
            template.setActive(!template.isActive());
            templateRepository.save(template);
            
            String status = template.isActive() ? "activé" : "désactivé";
            redirectAttributes.addFlashAttribute("success", "Template " + status + " avec succès");
            log.info("Template {} {}", id, status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            log.error("Erreur toggle template: {}", e.getMessage());
        }
        
        return "redirect:/admin/notification-templates";
    }
    
    /**
     * Supprimer un template
     */
    @PostMapping("/delete/{id}")
    public String deleteTemplate(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            templateRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Template supprimé avec succès");
            log.info("Template supprimé: {}", id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
            log.error("Erreur suppression template: {}", e.getMessage());
        }
        
        return "redirect:/admin/notification-templates";
    }
    
    /**
     * Retourne les variables disponibles pour chaque type de notification
     */
    private java.util.Map<String, java.util.List<VariableInfo>> getTemplateVariables() {
        java.util.Map<String, java.util.List<VariableInfo>> variables = new java.util.HashMap<>();
        
        // Variables communes à tous les templates
        java.util.List<VariableInfo> commonVars = java.util.List.of(
            new VariableInfo("prenom", "Prénom de l'utilisateur"),
            new VariableInfo("nom", "Nom de l'utilisateur"),
            new VariableInfo("email", "Email de l'utilisateur"),
            new VariableInfo("phone", "Téléphone de l'utilisateur")
        );
        
        // OTP_VERIFICATION
        variables.put("OTP_VERIFICATION", java.util.List.of(
            new VariableInfo("code", "Code OTP à 6 chiffres")
        ));
        
        // WELCOME
        variables.put("WELCOME", commonVars);
        
        // GROUP_INVITATION
        variables.put("GROUP_INVITATION", java.util.List.of(
            new VariableInfo("prenom", "Prénom de l'utilisateur"),
            new VariableInfo("groupe", "Nom du groupe"),
            new VariableInfo("inviteur", "Nom de la personne qui invite"),
            new VariableInfo("lien", "Lien d'invitation")
        ));
        
        // CONTRIBUTION_REMINDER
        variables.put("CONTRIBUTION_REMINDER", java.util.List.of(
            new VariableInfo("prenom", "Prénom de l'utilisateur"),
            new VariableInfo("groupe", "Nom du groupe"),
            new VariableInfo("montant", "Montant de la contribution"),
            new VariableInfo("date_limite", "Date limite de paiement"),
            new VariableInfo("lien", "Lien pour payer")
        ));
        
        // PAYMENT_SUCCESS
        variables.put("PAYMENT_SUCCESS", java.util.List.of(
            new VariableInfo("prenom", "Prénom de l'utilisateur"),
            new VariableInfo("montant", "Montant payé"),
            new VariableInfo("groupe", "Nom du groupe"),
            new VariableInfo("date", "Date du paiement"),
            new VariableInfo("reference", "Référence de transaction")
        ));
        
        // PAYMENT_FAILED
        variables.put("PAYMENT_FAILED", java.util.List.of(
            new VariableInfo("prenom", "Prénom de l'utilisateur"),
            new VariableInfo("montant", "Montant du paiement"),
            new VariableInfo("groupe", "Nom du groupe"),
            new VariableInfo("raison", "Raison de l'échec"),
            new VariableInfo("lien", "Lien pour réessayer")
        ));
        
        // PAYOUT_PROCESSED
        variables.put("PAYOUT_PROCESSED", java.util.List.of(
            new VariableInfo("prenom", "Prénom de l'utilisateur"),
            new VariableInfo("montant", "Montant du versement"),
            new VariableInfo("groupe", "Nom du groupe"),
            new VariableInfo("date", "Date du versement"),
            new VariableInfo("methode", "Méthode de paiement")
        ));
        
        // TOUR_REMINDER
        variables.put("TOUR_REMINDER", java.util.List.of(
            new VariableInfo("prenom", "Prénom de l'utilisateur"),
            new VariableInfo("groupe", "Nom du groupe"),
            new VariableInfo("date_tour", "Date du tour"),
            new VariableInfo("montant", "Montant à recevoir")
        ));
        
        // EXPORT_READY
        variables.put("EXPORT_READY", java.util.List.of(
            new VariableInfo("prenom", "Prénom de l'utilisateur"),
            new VariableInfo("type_export", "Type d'export (PDF, Excel)"),
            new VariableInfo("lien", "Lien de téléchargement")
        ));
        
        return variables;
    }
    
    /**
     * Classe interne pour représenter une variable de template
     */
    public static class VariableInfo {
        private final String name;
        private final String description;
        
        public VariableInfo(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getPlaceholder() { return "{{" + name + "}}"; }
    }
}
