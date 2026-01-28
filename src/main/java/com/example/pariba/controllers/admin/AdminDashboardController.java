package com.example.pariba.controllers.admin;

import com.example.pariba.models.Person;
import com.example.pariba.models.TontineGroup;
import com.example.pariba.repositories.*;
import com.example.pariba.services.SimpleDashboardStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour le dashboard administrateur (Thymeleaf)
 * Accessible uniquement aux SUPERADMIN
 * 
 * Note: Les ADMIN de groupe n'ont PAS accès à ce dashboard.
 * Les ADMIN de groupe gèrent uniquement leur groupe via l'API REST.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {
    
    private final SimpleDashboardStatsService dashboardStatsService;
    private final PersonRepository personRepository;
    private final TontineGroupRepository tontineGroupRepository;
    private final PaymentRepository paymentRepository;
    private final AuditLogRepository auditLogRepository;
    
    @Value("${server.port}")
    private String serverPort;
    
    /**
     * Page d'accueil du dashboard admin
     */
    @GetMapping({"/", "/dashboard"})
    @PreAuthorize("hasRole('SUPERADMIN')")
    public String dashboard(Model model, Authentication authentication) {
        log.info("✅ Accès au dashboard admin par: {}", authentication.getName());
        
        try {
            // Récupérer les statistiques globales
            Map<String, Object> stats = dashboardStatsService.getGlobalStats();
            
            model.addAttribute("pageTitle", "Dashboard Administrateur");
            model.addAttribute("stats", stats);
            model.addAttribute("username", authentication.getName());
            
            return "admin/dashboard";
        } catch (Exception e) {
            log.error("Erreur lors du chargement du dashboard", e);
            model.addAttribute("error", "Erreur lors du chargement des données");
            return "admin/error";
        }
    }
    
    /**
     * Page de connexion admin
     */
    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }
    
    /**
     * Page de gestion des utilisateurs
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Transactional(readOnly = true)
    public String users(Model model, 
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       @RequestParam(required = false) String search) {
        log.info("👥 Accès à la gestion des utilisateurs - Recherche: {}", search);
        
        try {
            var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Person> usersPage;
            
            if (search != null && !search.trim().isEmpty()) {
                // Recherche par nom, prénom, email ou téléphone
                usersPage = personRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContaining(
                    search, search, search, search, pageable);
            } else {
                usersPage = personRepository.findAll(pageable);
            }
            
            model.addAttribute("pageTitle", "Gestion des Utilisateurs");
            model.addAttribute("users", usersPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", usersPage.getTotalPages());
            model.addAttribute("totalUsers", usersPage.getTotalElements());
            model.addAttribute("search", search);
            
            return "admin/users";
        } catch (Exception e) {
            log.error("Erreur lors du chargement des utilisateurs", e);
            model.addAttribute("error", "Erreur lors du chargement des données");
            return "admin/error";
        }
    }
    
    /**
     * Page de gestion des groupes
     */
    @GetMapping("/groups")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Transactional(readOnly = true)
    public String groups(Model model,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size,
                        @RequestParam(required = false) String search) {
        log.info("👥 Accès à la gestion des groupes - Recherche: {}", search);
        
        try {
            var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            var groupsPage = (search != null && !search.trim().isEmpty()) 
                ? tontineGroupRepository.findByNomContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, pageable)
                : tontineGroupRepository.findAll(pageable);
            
            // Convertir en DTOs pour éviter LazyInitializationException
            var groupDtos = groupsPage.getContent().stream()
                .map(group -> {
                    var dto = new com.example.pariba.dtos.responses.GroupResponse();
                    dto.setId(group.getId());
                    dto.setNom(group.getNom());
                    dto.setDescription(group.getDescription());
                    dto.setMontant(group.getMontant());
                    dto.setFrequency(group.getFrequency());
                    dto.setTotalTours(group.getTotalTours());
                    dto.setStartDate(group.getStartDate());
                    dto.setCreatedAt(group.getCreatedAt());
                    
                    // Charger le créateur
                    if (group.getCreator() != null) {
                        var creatorDto = new com.example.pariba.dtos.responses.PersonResponse();
                        creatorDto.setPrenom(group.getCreator().getPrenom());
                        creatorDto.setNom(group.getCreator().getNom());
                        dto.setCreator(creatorDto);
                    }
                    
                    return dto;
                })
                .toList();
            
            model.addAttribute("pageTitle", "Gestion des Groupes");
            model.addAttribute("groups", groupDtos);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", groupsPage.getTotalPages());
            model.addAttribute("totalGroups", groupsPage.getTotalElements());
            model.addAttribute("search", search);
            
            return "admin/groups";
        } catch (Exception e) {
            log.error("Erreur lors du chargement des groupes", e);
            model.addAttribute("error", "Erreur lors du chargement des données");
            return "admin/error";
        }
    }
    
    /**
     * Page de gestion des paiements/transactions
     */
    @GetMapping("/payments")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Transactional(readOnly = true)
    public String payments(Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "20") int size,
                          @RequestParam(required = false) String search) {
        log.info("Accès à la gestion des paiements - Recherche: {}", search);
        
        try {
            var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            var paymentsPage = paymentRepository.findAll(pageable);
            
            // Convertir les entités Payment en PaymentResponse pour éviter LazyInitializationException
            var paymentResponses = paymentsPage.getContent().stream()
                .map(payment -> {
                    var response = new com.example.pariba.dtos.responses.PaymentResponse(payment);
                    return response;
                })
                .toList();
            
            model.addAttribute("pageTitle", "Gestion des Paiements");
            model.addAttribute("payments", paymentResponses);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", paymentsPage.getTotalPages());
            model.addAttribute("totalPayments", paymentsPage.getTotalElements());
            model.addAttribute("search", search);
            
            return "admin/payments";
        } catch (Exception e) {
            log.error("Erreur lors du chargement des paiements", e);
            model.addAttribute("error", "Erreur lors du chargement des données");
            return "admin/error";
        }
    }
    
    /**
     * Page des rapports et analyses
     */
    @GetMapping("/reports")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public String reports(Model model) {
        log.info("Accès aux rapports");
        
        try {
            Map<String, Object> stats = dashboardStatsService.getGlobalStats();
            
            model.addAttribute("pageTitle", "Rapports et Analyses");
            model.addAttribute("stats", stats);
            
            return "admin/reports";
        } catch (Exception e) {
            log.error("Erreur lors du chargement des rapports", e);
            model.addAttribute("error", "Erreur lors du chargement des données");
            return "admin/error";
        }
    }
    
    /**
     * Page des logs d'audit
     */
    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Transactional(readOnly = true)
    public String auditLogs(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "50") int size) {
        log.info("Accès aux logs d'audit");
        
        try {
            var pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
            var logsPage = auditLogRepository.findAll(pageable);
            
            // Convertir en DTOs simples pour éviter LazyInitializationException
            var logDtos = logsPage.getContent().stream()
                .map(auditLog -> {
                    var dto = new java.util.HashMap<String, Object>();
                    dto.put("id", auditLog.getId());
                    dto.put("action", auditLog.getAction());
                    dto.put("entityType", auditLog.getEntityType());
                    dto.put("entityId", auditLog.getEntityId());
                    dto.put("username", auditLog.getUsername());
                    dto.put("ipAddress", auditLog.getIpAddress());
                    dto.put("timestamp", auditLog.getTimestamp());
                    dto.put("details", auditLog.getDetails());
                    
                    // Charger l'acteur si présent
                    if (auditLog.getActor() != null) {
                        dto.put("actorName", auditLog.getActor().getPrenom() + " " + auditLog.getActor().getNom());
                    } else if (auditLog.getUsername() != null) {
                        // Si username ressemble à un UUID, essayer de charger la personne
                        String username = auditLog.getUsername();
                        if (username.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
                            try {
                                var person = personRepository.findById(username);
                                if (person.isPresent()) {
                                    dto.put("actorName", person.get().getPrenom() + " " + person.get().getNom());
                                } else {
                                    dto.put("actorName", "Utilisateur inconnu");
                                }
                            } catch (Exception e) {
                                dto.put("actorName", "Utilisateur inconnu");
                            }
                        } else {
                            dto.put("actorName", username);
                        }
                    } else {
                        dto.put("actorName", "Système");
                    }
                    
                    return dto;
                })
                .toList();
            
            model.addAttribute("pageTitle", "Logs d'Audit");
            model.addAttribute("logs", logDtos);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", logsPage.getTotalPages());
            model.addAttribute("totalLogs", logsPage.getTotalElements());
            
            return "admin/audit-logs";
        } catch (Exception e) {
            log.error("Erreur lors du chargement des logs", e);
            model.addAttribute("error", "Erreur lors du chargement des données");
            return "admin/error";
        }
    }
    
    /**
     * Page des paramètres système
     */
    @GetMapping("/settings")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public String settings(Model model) {
        log.info(" Accès aux paramètres système");
        
        model.addAttribute("pageTitle", "Paramètres Système");
        model.addAttribute("serverPort", serverPort);
        model.addAttribute("baseUrl", "http://localhost");
        
        return "admin/settings";
    }
    
    /**
     * Page d'envoi de notifications
     */
    @GetMapping("/send-notification")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public String sendNotificationPage(Model model) {
        log.info("📧 Accès à la page d'envoi de notifications");
        
        // Récupérer tous les utilisateurs pour la liste déroulante
        List<Person> users = personRepository.findAll();
        
        // Récupérer tous les groupes pour la liste déroulante
        List<TontineGroup> groups = tontineGroupRepository.findAll();
        
        model.addAttribute("pageTitle", "Envoyer une Notification");
        model.addAttribute("users", users);
        model.addAttribute("groups", groups);
        
        return "admin/send-notification";
    }
}
