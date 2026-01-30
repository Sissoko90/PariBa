package com.example.pariba.controllers.admin;

import com.example.pariba.models.Person;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.services.IPasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur pour la réinitialisation et le changement de mot de passe admin
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminPasswordResetController {
    
    private final IPasswordResetService passwordResetService;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Page "Mot de passe oublié"
     */
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "admin/forgot-password";
    }
    
    /**
     * Traiter la demande de réinitialisation
     */
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        log.info("🔑 Demande de réinitialisation de mot de passe pour: {}", email);
        
        try {
            // Validation de l'email
            if (email == null || email.trim().isEmpty()) {
                log.warn("❌ Email vide");
                redirectAttributes.addAttribute("error", "empty");
                return "redirect:/admin/forgot-password";
            }
            
            // Vérifier que l'email existe
            Person person = personRepository.findByEmail(email.trim()).orElse(null);
            
            if (person == null) {
                log.warn("❌ Aucun compte trouvé pour l'email: {}", email);
                redirectAttributes.addAttribute("error", "notfound");
                redirectAttributes.addAttribute("email", email);
                return "redirect:/admin/forgot-password";
            }
            
            // Vérifier que c'est bien un SUPERADMIN
            if (person.getRole() == null || !"SUPERADMIN".equals(person.getRole().name())) {
                log.warn("❌ Tentative de réinitialisation pour un compte non-SUPERADMIN: {} (rôle: {})", 
                         email, person.getRole() != null ? person.getRole().name() : "NULL");
                redirectAttributes.addAttribute("error", "notadmin");
                redirectAttributes.addAttribute("email", email);
                return "redirect:/admin/forgot-password";
            }
            
            // Envoyer l'email de réinitialisation
            passwordResetService.sendResetPasswordEmail(email);
            
            log.info("✅ Email de réinitialisation envoyé à: {}", email);
            redirectAttributes.addAttribute("success", "true");
            return "redirect:/admin/forgot-password";
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'email de réinitialisation pour: {}", email, e);
            redirectAttributes.addAttribute("error", "server");
            redirectAttributes.addAttribute("message", e.getMessage());
            return "redirect:/admin/forgot-password";
        }
    }
    
    /**
     * Page de réinitialisation avec token
     */
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        log.info("🔑 Accès à la page de réinitialisation avec token");
        
        // Vérifier que le token est valide
        boolean isValid = passwordResetService.validateResetToken(token);
        
        if (!isValid) {
            log.warn("❌ Token de réinitialisation invalide ou expiré");
            return "redirect:/admin/login?error=invalid_token";
        }
        
        model.addAttribute("token", token);
        return "admin/reset-password";
    }
    
    /**
     * Traiter la réinitialisation du mot de passe
     */
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        log.info("🔑 Traitement de la réinitialisation de mot de passe");
        
        try {
            // Vérifier que les mots de passe correspondent
            if (!password.equals(confirmPassword)) {
                log.warn("❌ Les mots de passe ne correspondent pas");
                redirectAttributes.addAttribute("token", token);
                redirectAttributes.addAttribute("error", "mismatch");
                return "redirect:/admin/reset-password";
            }
            
            // Vérifier la longueur minimale
            if (password.length() < 8) {
                log.warn("❌ Mot de passe trop court");
                redirectAttributes.addAttribute("token", token);
                redirectAttributes.addAttribute("error", "weak");
                return "redirect:/admin/reset-password";
            }
            
            // Réinitialiser le mot de passe
            boolean success = passwordResetService.resetPassword(token, password);
            
            if (!success) {
                log.warn("❌ Échec de la réinitialisation (token invalide ou expiré)");
                redirectAttributes.addAttribute("error", "expired");
                return "redirect:/admin/login";
            }
            
            log.info("✅ Mot de passe réinitialisé avec succès");
            return "redirect:/admin/login?reset=success";
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la réinitialisation du mot de passe", e);
            redirectAttributes.addAttribute("token", token);
            redirectAttributes.addAttribute("error", "server");
            return "redirect:/admin/reset-password";
        }
    }
    
    /**
     * Page de changement de mot de passe (pour admin connecté)
     */
    @GetMapping("/change-password")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public String changePasswordPage() {
        return "admin/change-password";
    }
    
    /**
     * Traiter le changement de mot de passe
     */
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public String processChangePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        String username = authentication.getName();
        log.info("🔑 Changement de mot de passe pour: {}", username);
        
        try {
            // Récupérer l'utilisateur
            Person person = personRepository.findByEmail(username)
                .orElseGet(() -> personRepository.findByPhone(username).orElse(null));
            
            if (person == null || person.getUser() == null) {
                log.error("❌ Utilisateur non trouvé: {}", username);
                redirectAttributes.addAttribute("error", "user");
                return "redirect:/admin/change-password";
            }
            
            // Vérifier le mot de passe actuel
            if (!passwordEncoder.matches(currentPassword, person.getUser().getPassword())) {
                log.warn("❌ Mot de passe actuel incorrect pour: {}", username);
                redirectAttributes.addAttribute("error", "current");
                return "redirect:/admin/change-password";
            }
            
            // Vérifier que les nouveaux mots de passe correspondent
            if (!newPassword.equals(confirmPassword)) {
                log.warn("❌ Les nouveaux mots de passe ne correspondent pas");
                redirectAttributes.addAttribute("error", "mismatch");
                return "redirect:/admin/change-password";
            }
            
            // Vérifier la longueur minimale
            if (newPassword.length() < 8) {
                log.warn("❌ Nouveau mot de passe trop court");
                redirectAttributes.addAttribute("error", "weak");
                return "redirect:/admin/change-password";
            }
            
            // Vérifier que le nouveau mot de passe est différent
            if (passwordEncoder.matches(newPassword, person.getUser().getPassword())) {
                log.warn("❌ Le nouveau mot de passe est identique à l'ancien");
                redirectAttributes.addAttribute("error", "same");
                return "redirect:/admin/change-password";
            }
            
            // Changer le mot de passe
            person.getUser().setPassword(passwordEncoder.encode(newPassword));
            personRepository.save(person);
            
            log.info("✅ Mot de passe changé avec succès pour: {}", username);
            redirectAttributes.addAttribute("success", "true");
            return "redirect:/admin/change-password";
            
        } catch (Exception e) {
            log.error("❌ Erreur lors du changement de mot de passe", e);
            redirectAttributes.addAttribute("error", "server");
            return "redirect:/admin/change-password";
        }
    }
}
