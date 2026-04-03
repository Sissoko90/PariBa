package com.example.pariba.controllers.admin;

import com.example.pariba.dtos.requests.SubscriptionPlanRequest;
import com.example.pariba.dtos.responses.SubscriptionPlanResponse;
import com.example.pariba.enums.SubscriptionStatus;
import com.example.pariba.models.Subscription;
import com.example.pariba.models.SubscriptionPlan;
import com.example.pariba.repositories.SubscriptionRepository;
import com.example.pariba.repositories.SubscriptionPlanRepository;
import com.example.pariba.security.CurrentUser;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.ISystemLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.transaction.annotation.Transactional;
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
@Tag(name = "Admin - Plans d'abonnement", description = "Gestion des plans d'abonnement par l'administrateur")
public class AdminSubscriptionManagementController {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final IAuditService auditService;
    private final CurrentUser currentUser;
    private final ISystemLogService systemLogService;

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
    @Transactional(readOnly = true)
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
                subscriptionsPage = subscriptionRepository.findByStatusWithPersonAndPlan(subscriptionStatus, pageRequest);
            } else {
                subscriptionsPage = subscriptionRepository.findAllWithPersonAndPlan(pageRequest);
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
    @Transactional(readOnly = true)
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
            
            // Total par plan - utiliser l'ID comme clé pour éviter les conflits de noms
            Map<String, Long> byPlanId = new HashMap<>();
            List<SubscriptionPlan> allPlans = subscriptionPlanRepository.findAll();
            log.info("📊 Nombre de plans: {}", allPlans.size());
            
            for (SubscriptionPlan plan : allPlans) {
                long count = subscriptionRepository.countByPlanAndStatus(plan, SubscriptionStatus.ACTIVE);
                log.info("📊 Plan '{}' (ID: {}): {} abonnés actifs", plan.getName(), plan.getId(), count);
                byPlanId.put(plan.getId(), count);
            }
            
            stats.put("byPlanId", byPlanId);
            
            // Abonnements récents (30 derniers jours)
            Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
            long recentSubscriptions = subscriptionRepository.countByCreatedAtAfter(thirtyDaysAgo);
            stats.put("recentSubscriptions", recentSubscriptions);
            
            // Taux de rétention (approximatif)
            // Si on a des abonnements actifs et aucun expiré, le taux est 100%
            // Sinon, c'est le ratio actifs / (actifs + expirés)
            long totalExpired = subscriptionRepository.countByStatus(SubscriptionStatus.EXPIRED);
            long totalActive = subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE);
            double renewalRate;
            if (totalActive > 0 && totalExpired == 0) {
                renewalRate = 100.0;
            } else if (totalActive + totalExpired > 0) {
                renewalRate = (double) totalActive / (totalActive + totalExpired) * 100;
            } else {
                renewalRate = 0.0;
            }
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
    @PreAuthorize("hasRole('SUPERADMIN')")
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
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<SubscriptionPlanResponse> getPlanById(@PathVariable String id) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan non trouvé"));
        return ResponseEntity.ok(new SubscriptionPlanResponse(plan));
    }

    /**
     * API JSON: Créer un nouveau plan d'abonnement
     */
    @PostMapping(value = "/subscription-plans", consumes = "application/json")
    @ResponseBody
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Créer un plan", description = "Crée un nouveau plan d'abonnement")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Plan créé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides")
    })
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
        // Limites et fonctionnalités premium
        plan.setMaxGroups(request.getMaxGroups() != null ? request.getMaxGroups() : 2);
        plan.setCanExportPdf(request.getCanExportPdf() != null ? request.getCanExportPdf() : false);
        plan.setCanExportExcel(request.getCanExportExcel() != null ? request.getCanExportExcel() : false);
        plan = subscriptionPlanRepository.save(plan);
        
        // Audit log
        String adminId = currentUser.getPersonId();
        String details = String.format("{\"planId\":\"%s\",\"planName\":\"%s\",\"planType\":\"%s\",\"price\":\"%s\"}", 
            plan.getId(), plan.getName(), plan.getType(), plan.getMonthlyPrice());
        auditService.log(adminId, "SUBSCRIPTION_PLAN_CREATED", "SubscriptionPlan", plan.getId(), details);
        systemLogService.log(adminId, "Admin", "SUBSCRIPTION_PLAN_CREATED", "SubscriptionPlan", plan.getId(), details, "INFO", true);
        
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
            // Limites et fonctionnalités premium
            plan.setMaxGroups(request.getMaxGroups() != null ? request.getMaxGroups() : 2);
            plan.setCanExportPdf(request.getCanExportPdf() != null ? request.getCanExportPdf() : false);
            plan.setCanExportExcel(request.getCanExportExcel() != null ? request.getCanExportExcel() : false);
            
            plan = subscriptionPlanRepository.save(plan);
            
            // Audit log
            String adminId = currentUser.getPersonId();
            String details = String.format("{\"planId\":\"%s\",\"planName\":\"%s\",\"planType\":\"%s\",\"price\":\"%s\"}", 
                plan.getId(), plan.getName(), plan.getType(), plan.getMonthlyPrice());
            auditService.log(adminId, "SUBSCRIPTION_PLAN_CREATED", "SubscriptionPlan", plan.getId(), details);
            systemLogService.log(adminId, "Admin", "SUBSCRIPTION_PLAN_CREATED", "SubscriptionPlan", plan.getId(), details, "INFO", true);
            
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
    @Operation(summary = "Modifier un plan", description = "Met à jour un plan d'abonnement existant")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Plan mis à jour"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Plan non trouvé")
    })
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
        // Limites et fonctionnalités premium
        if (request.getMaxGroups() != null) {
            plan.setMaxGroups(request.getMaxGroups());
        }
        if (request.getCanExportPdf() != null) {
            plan.setCanExportPdf(request.getCanExportPdf());
        }
        if (request.getCanExportExcel() != null) {
            plan.setCanExportExcel(request.getCanExportExcel());
        }
        
        plan = subscriptionPlanRepository.save(plan);
        
        // Audit log
        String adminId = currentUser.getPersonId();
        String details = String.format("{\"planId\":\"%s\",\"planName\":\"%s\",\"planType\":\"%s\",\"price\":\"%s\"}", 
            plan.getId(), plan.getName(), plan.getType(), plan.getMonthlyPrice());
        auditService.log(adminId, "SUBSCRIPTION_PLAN_UPDATED", "SubscriptionPlan", plan.getId(), details);
        systemLogService.log(adminId, "Admin", "SUBSCRIPTION_PLAN_UPDATED", "SubscriptionPlan", plan.getId(), details, "INFO", true);
        
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
        
        // Audit log
        String adminId = currentUser.getPersonId();
        String details = String.format("{\"planId\":\"%s\",\"planName\":\"%s\",\"active\":\"%s\"}", 
            plan.getId(), plan.getName(), plan.getActive());
        auditService.log(adminId, "SUBSCRIPTION_PLAN_TOGGLED", "SubscriptionPlan", plan.getId(), details);
        systemLogService.log(adminId, "Admin", "SUBSCRIPTION_PLAN_TOGGLED", "SubscriptionPlan", plan.getId(), details, "INFO", true);
        
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
        
        // Audit log avant suppression
        String adminId = currentUser.getPersonId();
        String details = String.format("{\"planId\":\"%s\",\"planName\":\"%s\",\"planType\":\"%s\"}", 
            plan.getId(), plan.getName(), plan.getType());
        auditService.log(adminId, "SUBSCRIPTION_PLAN_DELETED", "SubscriptionPlan", plan.getId(), details);
        systemLogService.log(adminId, "Admin", "SUBSCRIPTION_PLAN_DELETED", "SubscriptionPlan", plan.getId(), details, "WARNING", true);
        
        subscriptionPlanRepository.delete(plan);
        
        return ResponseEntity.noContent().build();
    }
}
