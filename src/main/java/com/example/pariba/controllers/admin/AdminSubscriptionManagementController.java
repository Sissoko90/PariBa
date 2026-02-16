package com.example.pariba.controllers.admin;

import com.example.pariba.dtos.requests.SubscriptionPlanRequest;
import com.example.pariba.dtos.responses.SubscriptionPlanResponse;
import com.example.pariba.enums.SubscriptionStatus;
import com.example.pariba.models.Subscription;
import com.example.pariba.models.SubscriptionPlan;
import com.example.pariba.repositories.SubscriptionRepository;
import com.example.pariba.repositories.SubscriptionPlanRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur unifié pour la gestion complète des abonnements
 * Combine les API REST et les vues Thymeleaf
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminSubscriptionManagementController {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionRepository subscriptionRepository;

    // ========================================
    // VUES THYMELEAF - Plans d'Abonnement
    // ========================================

    /**
     * Page de gestion des plans d'abonnement (Thymeleaf)
     */
    @GetMapping("/subscription-plans-view")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public String subscriptionPlansView(Model model) {
        log.info("📋 Accès à la gestion des plans d'abonnement");

        try {
            List<SubscriptionPlanResponse> plans = subscriptionPlanRepository.findAll()
                    .stream()
                    .map(SubscriptionPlanResponse::new)
                    .collect(Collectors.toList());

            model.addAttribute("pageTitle", "Plans d'Abonnement");
            model.addAttribute("plans", plans);

            return "admin/subscription-plans";
        } catch (Exception e) {
            log.error("Erreur lors du chargement des plans d'abonnement", e);
            model.addAttribute("error", "Erreur lors du chargement des données");
            return "admin/error";
        }
    }

    // ========================================
    // VUES THYMELEAF - Abonnements Utilisateurs
    // ========================================

    /**
     * Page de gestion des abonnements utilisateurs (Thymeleaf)
     */
    @GetMapping("/subscriptions")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public String subscriptions(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size,
                               @RequestParam(required = false) String status) {
        log.info("📊 Accès à la gestion des abonnements");

        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Subscription> subscriptionsPage;

            if (status != null && !status.isEmpty()) {
                SubscriptionStatus subscriptionStatus = SubscriptionStatus.valueOf(status);
                subscriptionsPage = subscriptionRepository.findByStatus(subscriptionStatus, pageRequest);
            } else {
                subscriptionsPage = subscriptionRepository.findAll(pageRequest);
            }

            // Statistiques
            Map<String, Long> stats = new HashMap<>();
            stats.put("total", subscriptionRepository.count());
            stats.put("active", subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE));
            stats.put("expired", subscriptionRepository.countByStatus(SubscriptionStatus.EXPIRED));
            stats.put("cancelled", subscriptionRepository.countByStatus(SubscriptionStatus.CANCELLED));
            
            // Abonnements expirant bientôt (dans les 7 prochains jours)
            LocalDate now = LocalDate.now();
            LocalDate sevenDaysLater = now.plusDays(7);
            long expiringSoon = subscriptionRepository.countByStatusAndEndDateBetween(
                SubscriptionStatus.ACTIVE, now, sevenDaysLater
            );
            stats.put("expiringSoon", expiringSoon);

            model.addAttribute("pageTitle", "Gestion des Abonnements");
            model.addAttribute("subscriptions", subscriptionsPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", subscriptionsPage.getTotalPages());
            model.addAttribute("totalItems", subscriptionsPage.getTotalElements());
            model.addAttribute("stats", stats);
            model.addAttribute("selectedStatus", status);

            return "admin/subscriptions";
        } catch (Exception e) {
            log.error("Erreur lors du chargement des abonnements", e);
            model.addAttribute("error", "Erreur lors du chargement des données");
            return "admin/error";
        }
    }

    /**
     * Page des statistiques d'abonnement (Thymeleaf)
     */
    @GetMapping("/subscription-stats")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public String subscriptionStats(Model model) {
        log.info("📈 Accès aux statistiques d'abonnement");

        try {
            // Statistiques globales
            Map<String, Object> stats = new HashMap<>();
            
            // Total par statut
            stats.put("totalActive", subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE));
            stats.put("totalExpired", subscriptionRepository.countByStatus(SubscriptionStatus.EXPIRED));
            stats.put("totalCancelled", subscriptionRepository.countByStatus(SubscriptionStatus.CANCELLED));
            stats.put("totalCanceled", subscriptionRepository.countByStatus(SubscriptionStatus.CANCELED));
            
            // Total par plan
            Map<String, Long> byPlan = new HashMap<>();
            subscriptionPlanRepository.findAll().forEach(plan -> {
                long count = subscriptionRepository.countByPlanAndStatus(plan, SubscriptionStatus.ACTIVE);
                byPlan.put(plan.getName(), count);
            });
            stats.put("byPlan", byPlan);
            
            // Abonnements récents (30 derniers jours)
            Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
            long recentSubscriptions = subscriptionRepository.countByCreatedAtAfter(thirtyDaysAgo);
            stats.put("recentSubscriptions", recentSubscriptions);
            
            // Taux de renouvellement (approximatif)
            long totalExpired = subscriptionRepository.countByStatus(SubscriptionStatus.EXPIRED);
            long totalActive = subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE);
            double renewalRate = totalExpired > 0 ? (double) totalActive / (totalActive + totalExpired) * 100 : 0;
            stats.put("renewalRate", String.format("%.1f", renewalRate));
            
            // Revenus estimés (basé sur les abonnements actifs)
            double estimatedRevenue = 0.0;
            List<Subscription> activeSubscriptions = subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE);
            for (Subscription sub : activeSubscriptions) {
                estimatedRevenue += sub.getPlan().getPrice().doubleValue();
            }
            stats.put("estimatedMonthlyRevenue", estimatedRevenue);

            model.addAttribute("pageTitle", "Statistiques d'Abonnement");
            model.addAttribute("stats", stats);
            model.addAttribute("plans", subscriptionPlanRepository.findAll());

            return "admin/subscription-stats";
        } catch (Exception e) {
            log.error("Erreur lors du chargement des statistiques", e);
            model.addAttribute("error", "Erreur lors du chargement des données");
            return "admin/error";
        }
    }

    // ========================================
    // API REST - Plans d'Abonnement
    // ========================================

    /**
     * API: Obtenir tous les plans d'abonnement
     */
    @GetMapping("/subscription-plans")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubscriptionPlanResponse>> getAllPlans() {
        List<SubscriptionPlanResponse> plans = subscriptionPlanRepository.findAll()
                .stream()
                .map(SubscriptionPlanResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(plans);
    }

    /**
     * API: Obtenir un plan par ID
     */
    @GetMapping("/subscription-plans/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionPlanResponse> getPlanById(@PathVariable String id) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan non trouvé"));
        return ResponseEntity.ok(new SubscriptionPlanResponse(plan));
    }

    /**
     * API: Créer un nouveau plan d'abonnement (JSON)
     */
    @PostMapping(value = "/subscription-plans", consumes = "application/json")
    @ResponseBody
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<SubscriptionPlanResponse> createPlanJson(
            @Valid @RequestBody SubscriptionPlanRequest request) {
        
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setType(request.getType());
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setMonthlyPrice(request.getMonthlyPrice());
        // Utiliser featuresJson si disponible, sinon features
        String featuresData = request.getFeaturesJson() != null ? request.getFeaturesJson() : request.getFeatures();
        plan.setFeaturesJson(featuresData);
        plan.setActive(request.getActive() != null ? request.getActive() : true);
        
        plan = subscriptionPlanRepository.save(plan);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SubscriptionPlanResponse(plan));
    }
    
    /**
     * Formulaire HTML: Créer un nouveau plan d'abonnement
     */
    @PostMapping(value = "/subscription-plans", consumes = "application/x-www-form-urlencoded")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public String createPlanForm(@Valid @ModelAttribute SubscriptionPlanRequest request, Model model) {
        try {
            SubscriptionPlan plan = new SubscriptionPlan();
            plan.setType(request.getType());
            plan.setName(request.getName());
            plan.setDescription(request.getDescription());
            plan.setMonthlyPrice(request.getMonthlyPrice());
            // Utiliser featuresJson si disponible, sinon features
            String featuresData = request.getFeaturesJson() != null ? request.getFeaturesJson() : request.getFeatures();
            plan.setFeaturesJson(featuresData);
            plan.setActive(request.getActive() != null ? request.getActive() : true);
            
            subscriptionPlanRepository.save(plan);
            
            return "redirect:/admin/subscription-plans-view?success=created";
        } catch (Exception e) {
            log.error("Erreur lors de la création du plan", e);
            model.addAttribute("error", "Erreur lors de la création du plan");
            return "redirect:/admin/subscription-plans-view?error=creation_failed";
        }
    }

    /**
     * API: Mettre à jour un plan d'abonnement
     */
    @PutMapping("/subscription-plans/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<SubscriptionPlanResponse> updatePlan(
            @PathVariable String id,
            @Valid @RequestBody SubscriptionPlanRequest request) {
        
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan non trouvé"));
        
        plan.setType(request.getType());
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setMonthlyPrice(request.getMonthlyPrice());
        // Utiliser featuresJson si disponible, sinon features
        String featuresData = request.getFeaturesJson() != null ? request.getFeaturesJson() : request.getFeatures();
        plan.setFeaturesJson(featuresData);
        if (request.getActive() != null) {
            plan.setActive(request.getActive());
        }
        
        plan = subscriptionPlanRepository.save(plan);
        
        return ResponseEntity.ok(new SubscriptionPlanResponse(plan));
    }

    /**
     * API: Activer/Désactiver un plan
     */
    @PatchMapping("/subscription-plans/{id}/toggle")
    @ResponseBody
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<SubscriptionPlanResponse> togglePlan(@PathVariable String id) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan non trouvé"));
        
        plan.setActive(!plan.getActive());
        plan = subscriptionPlanRepository.save(plan);
        
        return ResponseEntity.ok(new SubscriptionPlanResponse(plan));
    }

    /**
     * API: Supprimer un plan d'abonnement
     */
    @DeleteMapping("/subscription-plans/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> deletePlan(@PathVariable String id) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan non trouvé"));
        
        subscriptionPlanRepository.delete(plan);
        
        return ResponseEntity.noContent().build();
    }
}
