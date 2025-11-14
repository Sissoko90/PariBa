package com.example.pariba.controllers.admin;

import com.example.pariba.models.SystemLog;
import com.example.pariba.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour les statistiques SuperAdmin
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('SUPERADMIN')")
@Slf4j
public class AdminStatisticsController {
    
    private final PersonRepository personRepository;
    private final TontineGroupRepository groupRepository;
    private final PaymentRepository paymentRepository;
    private final ContributionRepository contributionRepository;
    private final NotificationRepository notificationRepository;
    private final SystemLogRepository systemLogRepository;
    
    public AdminStatisticsController(PersonRepository personRepository,
                                    TontineGroupRepository groupRepository,
                                    PaymentRepository paymentRepository,
                                    ContributionRepository contributionRepository,
                                    NotificationRepository notificationRepository,
                                    SystemLogRepository systemLogRepository) {
        this.personRepository = personRepository;
        this.groupRepository = groupRepository;
        this.paymentRepository = paymentRepository;
        this.contributionRepository = contributionRepository;
        this.notificationRepository = notificationRepository;
        this.systemLogRepository = systemLogRepository;
    }
    
    @GetMapping("/superadmin-dashboard")
    public String superAdminDashboard(Model model) {
        try {
            // Statistiques globales
            long totalUsers = personRepository.count();
            long totalGroups = groupRepository.count();
            long totalPayments = paymentRepository.count();
            long totalContributions = contributionRepository.count();
            
            // Statistiques des 24 dernières heures
            LocalDateTime last24h = LocalDateTime.now().minusHours(24);
            long newUsersToday = personRepository.countByCreatedAtAfter(last24h);
            long newGroupsToday = groupRepository.countByCreatedAtAfter(last24h);
            
            // Statistiques des 7 derniers jours
            LocalDateTime last7days = LocalDateTime.now().minusDays(7);
            long newUsersWeek = personRepository.countByCreatedAtAfter(last7days);
            long newGroupsWeek = groupRepository.countByCreatedAtAfter(last7days);
            
            // Statistiques des 30 derniers jours
            LocalDateTime last30days = LocalDateTime.now().minusDays(30);
            long newUsersMonth = personRepository.countByCreatedAtAfter(last30days);
            long newGroupsMonth = groupRepository.countByCreatedAtAfter(last30days);
            
            // Logs système récents
            List<SystemLog> recentLogs = systemLogRepository.findTop100ByOrderByCreatedAtDesc();
            long totalLogs = systemLogRepository.count();
            long errorsToday = systemLogRepository.countErrorsSince(last24h);
            
            // Tous les groupes (pas de distinction actif/inactif pour l'instant)
            // Note: TontineGroup n'a pas de champ 'active'
            long activeGroups = totalGroups;
            long inactiveGroups = 0;
            
            // Notifications
            long totalNotifications = notificationRepository.count();
            long unreadNotifications = notificationRepository.countByReadFlagFalse();
            
            // Ajouter au modèle
            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("totalGroups", totalGroups);
            model.addAttribute("totalPayments", totalPayments);
            model.addAttribute("totalContributions", totalContributions);
            
            model.addAttribute("newUsersToday", newUsersToday);
            model.addAttribute("newGroupsToday", newGroupsToday);
            model.addAttribute("newUsersWeek", newUsersWeek);
            model.addAttribute("newGroupsWeek", newGroupsWeek);
            model.addAttribute("newUsersMonth", newUsersMonth);
            model.addAttribute("newGroupsMonth", newGroupsMonth);
            
            model.addAttribute("activeGroups", activeGroups);
            model.addAttribute("inactiveGroups", inactiveGroups);
            
            model.addAttribute("totalNotifications", totalNotifications);
            model.addAttribute("unreadNotifications", unreadNotifications);
            
            model.addAttribute("recentLogs", recentLogs);
            model.addAttribute("totalLogs", totalLogs);
            model.addAttribute("errorsToday", errorsToday);
            
            model.addAttribute("pageTitle", "Dashboard SuperAdmin");
            
            // Données pour graphiques
            Map<String, Object> chartData = prepareChartData();
            model.addAttribute("chartData", chartData);
            
        } catch (Exception e) {
            log.error("Erreur lors du chargement du dashboard: {}", e.getMessage());
            model.addAttribute("error", "Erreur lors du chargement des statistiques");
        }
        
        return "admin/superadmin-dashboard";
    }
    
    @GetMapping("/system-logs")
    public String systemLogs(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "50") int size,
                            @RequestParam(required = false) String level,
                            @RequestParam(required = false) String search) {
        log.info("Accès aux logs système - Page: {}, Level: {}, Search: {}", page, level, search);
        
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(
                page, size, org.springframework.data.domain.Sort.by("createdAt").descending()
            );
            
            org.springframework.data.domain.Page<SystemLog> logsPage;
            
            // Recherche + Filtrage
            boolean hasSearch = search != null && !search.trim().isEmpty();
            boolean hasLevel = level != null && !level.trim().isEmpty() && !level.equals("ALL");
            
            if (hasSearch && hasLevel) {
                // Recherche avec filtre de niveau
                logsPage = systemLogRepository.searchLogsByLevel(level, search, pageable);
            } else if (hasSearch) {
                // Recherche sans filtre
                logsPage = systemLogRepository.searchLogs(search, pageable);
            } else if (hasLevel) {
                // Filtre de niveau sans recherche
                logsPage = systemLogRepository.findByLevelOrderByCreatedAtDesc(level, pageable);
            } else {
                // Aucun filtre
                logsPage = systemLogRepository.findByOrderByCreatedAtDesc(pageable);
            }
            
            model.addAttribute("logs", logsPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", logsPage.getTotalPages());
            model.addAttribute("totalLogs", logsPage.getTotalElements());
            model.addAttribute("selectedLevel", level != null ? level : "ALL");
            model.addAttribute("search", search);
            model.addAttribute("pageTitle", "Logs Système");
            
            return "admin/system-logs";
        } catch (Exception e) {
            log.error("Erreur lors du chargement des logs système", e);
            model.addAttribute("error", "Erreur lors du chargement des logs");
            return "admin/error";
        }
    }
    
    /**
     * Préparer les données pour les graphiques
     */
    private Map<String, Object> prepareChartData() {
        Map<String, Object> data = new HashMap<>();
        
        // Évolution des utilisateurs sur 7 jours
        Map<String, Long> userGrowth = new HashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime date = LocalDateTime.now().minusDays(i);
            LocalDateTime nextDate = date.plusDays(1);
            long count = personRepository.countByCreatedAtBetween(
                date.atZone(java.time.ZoneId.systemDefault()).toInstant(),
                nextDate.atZone(java.time.ZoneId.systemDefault()).toInstant()
            );
            userGrowth.put(date.toLocalDate().toString(), count);
        }
        data.put("userGrowth", userGrowth);
        
        // Évolution des groupes sur 7 jours
        Map<String, Long> groupGrowth = new HashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime date = LocalDateTime.now().minusDays(i);
            LocalDateTime nextDate = date.plusDays(1);
            long count = groupRepository.countByCreatedAtBetween(date, nextDate);
            groupGrowth.put(date.toLocalDate().toString(), count);
        }
        data.put("groupGrowth", groupGrowth);
        
        return data;
    }
}
