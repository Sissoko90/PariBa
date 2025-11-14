package com.example.pariba.controllers.admin;

import com.example.pariba.models.Advertisement;
import com.example.pariba.repositories.AdvertisementRepository;
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
 * Contrôleur admin pour gérer les publicités
 */
@Controller
@RequestMapping("/admin/advertisements")
@PreAuthorize("hasRole('SUPERADMIN')")
@Slf4j
public class AdminAdvertisementController {
    
    private final AdvertisementRepository advertisementRepository;
    
    public AdminAdvertisementController(AdvertisementRepository advertisementRepository) {
        this.advertisementRepository = advertisementRepository;
    }
    
    @GetMapping
    public String listAdvertisements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Page<Advertisement> ads = advertisementRepository.findAll(
            PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
        
        model.addAttribute("advertisements", ads.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ads.getTotalPages());
        model.addAttribute("totalItems", ads.getTotalElements());
        model.addAttribute("pageTitle", "Gestion des Publicités");
        
        return "admin/advertisements";
    }
    
    @GetMapping("/new")
    public String newAdvertisementForm(Model model) {
        model.addAttribute("advertisement", new Advertisement());
        model.addAttribute("pageTitle", "Nouvelle Publicité");
        model.addAttribute("isEdit", false);
        return "admin/advertisement-form";
    }
    
    @GetMapping("/edit/{id}")
    public String editAdvertisementForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        Advertisement ad = advertisementRepository.findById(id).orElse(null);
        
        if (ad == null) {
            redirectAttributes.addFlashAttribute("error", "Publicité non trouvée");
            return "redirect:/admin/advertisements";
        }
        
        model.addAttribute("advertisement", ad);
        model.addAttribute("pageTitle", "Modifier Publicité");
        model.addAttribute("isEdit", true);
        return "admin/advertisement-form";
    }
    
    @PostMapping
    public String createAdvertisement(@ModelAttribute Advertisement advertisement, RedirectAttributes redirectAttributes) {
        try {
            advertisement.setActive(true);
            advertisement.setClicks(0);
            advertisement.setImpressions(0);
            advertisementRepository.save(advertisement);
            
            redirectAttributes.addFlashAttribute("success", "Publicité créée avec succès");
            log.info(" Publicité créée: {}", advertisement.getTitle());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            log.error("Erreur création publicité: {}", e.getMessage());
        }
        
        return "redirect:/admin/advertisements";
    }
    
    @PostMapping("/update/{id}")
    public String updateAdvertisement(
            @PathVariable String id,
            @ModelAttribute Advertisement advertisement,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Advertisement existing = advertisementRepository.findById(id).orElse(null);
            
            if (existing == null) {
                redirectAttributes.addFlashAttribute("error", "Publicité non trouvée");
                return "redirect:/admin/advertisements";
            }
            
            existing.setTitle(advertisement.getTitle());
            existing.setDescription(advertisement.getDescription());
            existing.setImageUrl(advertisement.getImageUrl());
            existing.setLinkUrl(advertisement.getLinkUrl());
            existing.setStartDate(advertisement.getStartDate());
            existing.setEndDate(advertisement.getEndDate());
            existing.setActive(advertisement.isActive());
            
            advertisementRepository.save(existing);
            redirectAttributes.addFlashAttribute("success", "Publicité mise à jour");
            log.info("✅ Publicité mise à jour: {}", id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            log.error("❌ Erreur mise à jour publicité: {}", e.getMessage());
        }
        
        return "redirect:/admin/advertisements";
    }
    
    @PostMapping("/toggle/{id}")
    public String toggleAdvertisement(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            Advertisement ad = advertisementRepository.findById(id).orElse(null);
            
            if (ad == null) {
                redirectAttributes.addFlashAttribute("error", "Publicité non trouvée");
                return "redirect:/admin/advertisements";
            }
            
            ad.setActive(!ad.isActive());
            advertisementRepository.save(ad);
            
            String status = ad.isActive() ? "activée" : "désactivée";
            redirectAttributes.addFlashAttribute("success", "Publicité " + status);
            log.info("✅ Publicité {} {}", id, status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        
        return "redirect:/admin/advertisements";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteAdvertisement(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            advertisementRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Publicité supprimée");
            log.info("✅ Publicité supprimée: {}", id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            log.error("❌ Erreur suppression publicité: {}", e.getMessage());
        }
        
        return "redirect:/admin/advertisements";
    }
}
