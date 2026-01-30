package com.example.pariba.controllers.admin;

import com.example.pariba.dtos.responses.GuideResponse;
import com.example.pariba.enums.GuideCategory;
import com.example.pariba.services.IGuideService;
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
    
    public AdminGuideController(IGuideService guideService) {
        this.guideService = guideService;
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
            guideService.createGuide(guide);
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
            redirectAttributes.addFlashAttribute("success", "Guide mis à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/support/guides";
    }
    
    @PostMapping("/{id}/delete")
    public String deleteGuide(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            guideService.deleteGuide(id);
            redirectAttributes.addFlashAttribute("success", "Guide supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/support/guides";
    }
}
