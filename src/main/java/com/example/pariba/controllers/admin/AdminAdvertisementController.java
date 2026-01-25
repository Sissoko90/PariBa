package com.example.pariba.controllers.admin;

import com.example.pariba.models.Advertisement;
import com.example.pariba.repositories.AdvertisementRepository;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Contrôleur admin pour gérer les publicités
 */
@Controller
@RequestMapping("/admin/advertisements")
@PreAuthorize("hasRole('SUPERADMIN')")
@Slf4j
public class AdminAdvertisementController {
    
    private final AdvertisementRepository advertisementRepository;
    
    @Value("${app.upload.dir:uploads/advertisements}")
    private String uploadDir;
    
    @Value("${app.base-url:http://localhost:8085}")
    private String baseUrl;
    
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
    
    @PostMapping("/upload-image")
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Fichier vide"));
            }
            
            // Vérifier le type de fichier
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le fichier doit être une image"));
            }
            
            // Vérifier la taille (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("error", "L'image ne doit pas dépasser 5MB"));
            }
            
            // Créer le répertoire s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;
            
            // Sauvegarder le fichier
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Utiliser la configuration app.base-url
            String imageUrl = this.baseUrl + "/uploads/advertisements/" + filename;
            
            log.info("✅ Image uploadée: {}", filename);
            log.info("📍 URL générée: {}", imageUrl);
            log.info("🔧 Base URL configurée: {}", this.baseUrl);
            
            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            response.put("filename", filename);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("❌ Erreur upload image: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Erreur lors de l'upload: " + e.getMessage()));
        }
    }
    
    @PostMapping("/upload-video")
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadVideo(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Fichier vide"));
            }
            
            // Vérifier le type de fichier
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le fichier doit être une vidéo"));
            }
            
            // Vérifier la taille (max 50MB)
            if (file.getSize() > 50 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("error", "La vidéo ne doit pas dépasser 50MB"));
            }
            
            // Créer le répertoire s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".mp4";
            String filename = UUID.randomUUID().toString() + extension;
            
            // Sauvegarder le fichier
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Utiliser la configuration app.base-url
            String videoUrl = this.baseUrl + "/uploads/advertisements/" + filename;
            
            log.info("✅ Vidéo uploadée: {}", filename);
            log.info("📍 URL générée: {}", videoUrl);
            log.info("🔧 Base URL configurée: {}", this.baseUrl);
            
            Map<String, String> response = new HashMap<>();
            response.put("url", videoUrl);
            response.put("filename", filename);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("❌ Erreur upload vidéo: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Erreur lors de l'upload: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public String createAdvertisement(@ModelAttribute Advertisement advertisement, RedirectAttributes redirectAttributes) {
        try {
            // Vérifier que le placement est défini
            if (advertisement.getPlacement() == null) {
                redirectAttributes.addFlashAttribute("error", "L'emplacement est obligatoire");
                return "redirect:/admin/advertisements/new";
            }
            
            log.info("📥 Création publicité - Titre: {}, ImageURL reçue: {}", advertisement.getTitle(), advertisement.getImageUrl());
            
            // Décoder les entités HTML de l'URL (&#x2F; -> /, etc.)
            if (advertisement.getImageUrl() != null && !advertisement.getImageUrl().isEmpty()) {
                String decodedUrl = HtmlUtils.htmlUnescape(advertisement.getImageUrl());
                advertisement.setImageUrl(decodedUrl);
                log.info("🔄 URL décodée: {}", decodedUrl);
            }
            
            advertisement.setActive(true);
            advertisement.setClicks(0);
            advertisement.setImpressions(0);
            advertisementRepository.save(advertisement);
            
            log.info("💾 Publicité sauvegardée - ID: {}, ImageURL en DB: {}", advertisement.getId(), advertisement.getImageUrl());
            
            redirectAttributes.addFlashAttribute("success", "Publicité créée avec succès");
            log.info("✅ Publicité créée: {}", advertisement.getTitle());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            log.error("❌ Erreur création publicité: {}", e.getMessage());
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
            existing.setPlacement(advertisement.getPlacement());
            
            // Décoder les entités HTML de l'URL
            if (advertisement.getImageUrl() != null && !advertisement.getImageUrl().isEmpty()) {
                String decodedUrl = HtmlUtils.htmlUnescape(advertisement.getImageUrl());
                existing.setImageUrl(decodedUrl);
            }
            
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
