package com.example.pariba.controllers.admin;

import com.example.pariba.dtos.responses.GuideResponse;
import com.example.pariba.enums.GuideCategory;
import com.example.pariba.services.IGuideService;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.ISystemLogService;
import com.example.pariba.security.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/support/guides")
@PreAuthorize("hasRole('SUPERADMIN')")
public class AdminGuideController {
    
    private final IGuideService guideService;
    private final IAuditService auditService;
    private final ISystemLogService systemLogService;
    private final CurrentUser currentUser;
    
    public AdminGuideController(IGuideService guideService, IAuditService auditService, ISystemLogService systemLogService, CurrentUser currentUser) {
        this.guideService = guideService;
        this.auditService = auditService;
        this.systemLogService = systemLogService;
        this.currentUser = currentUser;
    }
    
    @GetMapping
    public String listGuides(Model model) {
        List<GuideResponse> guides = guideService.getAllGuides();
        model.addAttribute("guides", guides);
        model.addAttribute("categories", GuideCategory.values());
        return "admin/support/guides-list";
    }
    
    @GetMapping("/new")
    public String newGuideForm(Model model) {
        GuideResponse guide = new GuideResponse();
        guide.setActive(true);
        guide.setDisplayOrder(0);
        model.addAttribute("guide", guide);
        model.addAttribute("categories", GuideCategory.values());
        model.addAttribute("isEdit", false);
        return "admin/support/guide-form";
    }
    
    @GetMapping("/{id}/edit")
    public String editGuideForm(@PathVariable String id, Model model) {
        GuideResponse guide = guideService.getGuideById(id);
        model.addAttribute("guide", guide);
        model.addAttribute("categories", GuideCategory.values());
        model.addAttribute("isEdit", true);
        return "admin/support/guide-form";
    }
    
    @PostMapping("/new")
    public String createGuide(@ModelAttribute GuideResponse guide, RedirectAttributes redirectAttributes) {
        try {
            GuideResponse created = guideService.createGuide(guide);
            
            String adminId = currentUser.getPersonId();
            String details = String.format("{\"guideId\":\"%s\",\"title\":\"%s\"}", created.getId(), guide.getTitle());
            auditService.log(adminId, "GUIDE_CREATED", "Guide", created.getId(), details);
            systemLogService.log(adminId, "Admin", "GUIDE_CREATED", "Guide", created.getId(), details, "INFO", true);
            
            redirectAttributes.addFlashAttribute("success", "Guide créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/support/guides";
    }
    
    @PostMapping("/{id}/edit")
    public String updateGuide(@PathVariable String id, @ModelAttribute GuideResponse guide, RedirectAttributes redirectAttributes) {
        try {
            guideService.updateGuide(id, guide);
            
            String adminId = currentUser.getPersonId();
            String details = String.format("{\"guideId\":\"%s\",\"title\":\"%s\"}", id, guide.getTitle());
            auditService.log(adminId, "GUIDE_UPDATED", "Guide", id, details);
            systemLogService.log(adminId, "Admin", "GUIDE_UPDATED", "Guide", id, details, "INFO", true);
            
            redirectAttributes.addFlashAttribute("success", "Guide mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/support/guides";
    }
    
    @PostMapping("/{id}/delete")
    public String deleteGuide(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            String adminId = currentUser.getPersonId();
            String details = String.format("{\"guideId\":\"%s\"}", id);
            auditService.log(adminId, "GUIDE_DELETED", "Guide", id, details);
            systemLogService.log(adminId, "Admin", "GUIDE_DELETED", "Guide", id, details, "WARNING", true);
            
            guideService.deleteGuide(id);
            redirectAttributes.addFlashAttribute("success", "Guide supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/support/guides";
    }
}
