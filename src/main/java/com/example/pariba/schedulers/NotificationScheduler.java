package com.example.pariba.schedulers;

import com.example.pariba.enums.ContributionStatus;
import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.enums.TourStatus;
import com.example.pariba.models.Contribution;
import com.example.pariba.models.Tour;
import com.example.pariba.models.TontineGroup;
import com.example.pariba.repositories.ContributionRepository;
import com.example.pariba.repositories.TourRepository;
import com.example.pariba.repositories.TontineGroupRepository;
import com.example.pariba.services.INotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scheduler pour les notifications automatiques planifiées
 * - Rappels de contributions (2 jours avant, 1 jour avant, le jour même)
 * - Notifications de tours (démarrage, fin)
 * - Notifications de tontine (démarrage, fin)
 * - Pénalités de retard
 */
@Component
@Slf4j
public class NotificationScheduler {

    private final ContributionRepository contributionRepository;
    private final TourRepository tourRepository;
    private final TontineGroupRepository groupRepository;
    private final INotificationService notificationService;

    public NotificationScheduler(ContributionRepository contributionRepository,
                                TourRepository tourRepository,
                                TontineGroupRepository groupRepository,
                                INotificationService notificationService) {
        this.contributionRepository = contributionRepository;
        this.tourRepository = tourRepository;
        this.groupRepository = groupRepository;
        this.notificationService = notificationService;
    }

    /**
     * Exécuté tous les jours à 8h00
     * Envoie les rappels de contributions dues dans 2 jours
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendContributionReminders2Days() {
        log.info("🔔 Envoi des rappels de contributions (2 jours avant)...");
        
        LocalDate targetDate = LocalDate.now().plusDays(2);
        List<Contribution> contributions = contributionRepository
            .findByDueDateAndStatus(targetDate, ContributionStatus.DUE);
        
        for (Contribution contribution : contributions) {
            try {
                Map<String, String> variables = new HashMap<>();
                variables.put("groupe", contribution.getGroup().getNom());
                variables.put("montant", String.format("%,.0f", contribution.getAmountDue()));
                variables.put("date", targetDate.toString());
                
                notificationService.sendNotificationWithTemplate(
                    contribution.getMember().getId(),
                    NotificationType.CONTRIBUTION_REMINDER_2DAYS,
                    NotificationChannel.PUSH,
                    variables
                );
                
                log.info("✅ Rappel 2 jours envoyé à {} pour {}", 
                    contribution.getMember().getPhone(), contribution.getGroup().getNom());
            } catch (Exception e) {
                log.error("❌ Erreur rappel 2 jours pour contribution {}: {}", 
                    contribution.getId(), e.getMessage());
            }
        }
        
        log.info("✅ {} rappels de contributions (2 jours) envoyés", contributions.size());
    }

    /**
     * Exécuté tous les jours à 9h00
     * Envoie les rappels de contributions dues dans 1 jour
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendContributionReminders1Day() {
        log.info("🔔 Envoi des rappels de contributions (1 jour avant)...");
        
        LocalDate targetDate = LocalDate.now().plusDays(1);
        List<Contribution> contributions = contributionRepository
            .findByDueDateAndStatus(targetDate, ContributionStatus.DUE);
        
        for (Contribution contribution : contributions) {
            try {
                Map<String, String> variables = new HashMap<>();
                variables.put("groupe", contribution.getGroup().getNom());
                variables.put("montant", String.format("%,.0f", contribution.getAmountDue()));
                variables.put("date", targetDate.toString());
                
                notificationService.sendNotificationWithTemplate(
                    contribution.getMember().getId(),
                    NotificationType.CONTRIBUTION_REMINDER_1DAY,
                    NotificationChannel.PUSH,
                    variables
                );
                
                // Envoyer aussi par SMS pour plus d'urgence
                notificationService.sendNotificationWithTemplate(
                    contribution.getMember().getId(),
                    NotificationType.CONTRIBUTION_REMINDER_1DAY,
                    NotificationChannel.SMS,
                    variables
                );
                
                log.info("✅ Rappel 1 jour envoyé à {} pour {}", 
                    contribution.getMember().getPhone(), contribution.getGroup().getNom());
            } catch (Exception e) {
                log.error("❌ Erreur rappel 1 jour pour contribution {}: {}", 
                    contribution.getId(), e.getMessage());
            }
        }
        
        log.info("✅ {} rappels de contributions (1 jour) envoyés", contributions.size());
    }

    /**
     * Exécuté tous les jours à 10h00
     * Envoie les rappels de contributions dues aujourd'hui
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendContributionDueToday() {
        log.info("🔔 Envoi des rappels de contributions (aujourd'hui)...");
        
        LocalDate today = LocalDate.now();
        List<Contribution> contributions = contributionRepository
            .findByDueDateAndStatus(today, ContributionStatus.DUE);
        
        for (Contribution contribution : contributions) {
            try {
                Map<String, String> variables = new HashMap<>();
                variables.put("groupe", contribution.getGroup().getNom());
                variables.put("montant", String.format("%,.0f", contribution.getAmountDue()));
                variables.put("lien", "https://pariba.app/pay/" + contribution.getId());
                
                notificationService.sendNotificationWithTemplate(
                    contribution.getMember().getId(),
                    NotificationType.CONTRIBUTION_DUE_TODAY,
                    NotificationChannel.PUSH,
                    variables
                );
                
                // Envoyer aussi par SMS
                notificationService.sendNotificationWithTemplate(
                    contribution.getMember().getId(),
                    NotificationType.CONTRIBUTION_DUE_TODAY,
                    NotificationChannel.SMS,
                    variables
                );
                
                log.info("✅ Rappel aujourd'hui envoyé à {} pour {}", 
                    contribution.getMember().getPhone(), contribution.getGroup().getNom());
            } catch (Exception e) {
                log.error("❌ Erreur rappel aujourd'hui pour contribution {}: {}", 
                    contribution.getId(), e.getMessage());
            }
        }
        
        log.info("✅ {} rappels de contributions (aujourd'hui) envoyés", contributions.size());
    }

    /**
     * Exécuté tous les jours à 18h00
     * Envoie les notifications de contributions en retard
     */
    @Scheduled(cron = "0 0 18 * * ?")
    public void sendOverdueContributionNotifications() {
        log.info("🔔 Envoi des notifications de contributions en retard...");
        
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Contribution> contributions = contributionRepository
            .findByDueDateBeforeAndStatus(yesterday, ContributionStatus.DUE);
        
        for (Contribution contribution : contributions) {
            try {
                // Marquer comme en retard
                contribution.setStatus(ContributionStatus.LATE);
                contributionRepository.save(contribution);
                
                Map<String, String> variables = new HashMap<>();
                variables.put("groupe", contribution.getGroup().getNom());
                variables.put("montant", String.format("%,.0f", contribution.getAmountDue()));
                variables.put("lien", "https://pariba.app/pay/" + contribution.getId());
                
                notificationService.sendNotificationWithTemplate(
                    contribution.getMember().getId(),
                    NotificationType.CONTRIBUTION_OVERDUE,
                    NotificationChannel.PUSH,
                    variables
                );
                
                // Envoyer aussi par SMS et Email
                notificationService.sendNotificationWithTemplate(
                    contribution.getMember().getId(),
                    NotificationType.CONTRIBUTION_OVERDUE,
                    NotificationChannel.SMS,
                    variables
                );
                
                notificationService.sendNotificationWithTemplate(
                    contribution.getMember().getId(),
                    NotificationType.CONTRIBUTION_OVERDUE,
                    NotificationChannel.EMAIL,
                    variables
                );
                
                log.info("✅ Notification retard envoyée à {} pour {}", 
                    contribution.getMember().getPhone(), contribution.getGroup().getNom());
            } catch (Exception e) {
                log.error("❌ Erreur notification retard pour contribution {}: {}", 
                    contribution.getId(), e.getMessage());
            }
        }
        
        log.info("✅ {} notifications de retard envoyées", contributions.size());
    }

    /**
     * Exécuté tous les jours à 7h00
     * Envoie les notifications de tours qui commencent dans 2 jours
     */
    @Scheduled(cron = "0 0 7 * * ?")
    public void sendTourStartingSoonNotifications() {
        log.info("🔔 Envoi des notifications de tours qui commencent bientôt...");
        
        LocalDate targetDate = LocalDate.now().plusDays(2);
        List<Tour> tours = tourRepository.findByScheduledDateAndStatus(targetDate, TourStatus.SCHEDULED);
        
        for (Tour tour : tours) {
            try {
                TontineGroup group = tour.getGroup();
                
                // Notifier tous les membres du groupe
                group.getMemberships().forEach(membership -> {
                    try {
                        Map<String, String> variables = new HashMap<>();
                        variables.put("groupe", group.getNom());
                        variables.put("tour", "Tour " + tour.getIndexInGroup());
                        variables.put("jours", "2");
                        variables.put("beneficiaire", tour.getBeneficiary().getPrenom() + " " + tour.getBeneficiary().getNom());
                        variables.put("date", targetDate.toString());
                        
                        notificationService.sendNotificationWithTemplate(
                            membership.getPerson().getId(),
                            NotificationType.TOUR_STARTING_SOON,
                            NotificationChannel.PUSH,
                            variables
                        );
                    } catch (Exception e) {
                        log.error("Erreur notification tour pour membre {}", membership.getPerson().getId());
                    }
                });
                
                // Notifier spécialement le bénéficiaire
                Map<String, String> beneficiaryVars = new HashMap<>();
                beneficiaryVars.put("groupe", group.getNom());
                
                notificationService.sendNotificationWithTemplate(
                    tour.getBeneficiary().getId(),
                    NotificationType.YOUR_TURN_NEXT,
                    NotificationChannel.PUSH,
                    beneficiaryVars
                );
                
                log.info("✅ Notifications tour bientôt envoyées pour {}", group.getNom());
            } catch (Exception e) {
                log.error("❌ Erreur notification tour {}: {}", tour.getId(), e.getMessage());
            }
        }
        
        log.info("✅ {} notifications de tours bientôt envoyées", tours.size());
    }

    /**
     * Exécuté tous les jours à 8h00
     * Envoie les notifications de tours qui commencent aujourd'hui
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendTourStartedNotifications() {
        log.info("🔔 Envoi des notifications de tours qui démarrent aujourd'hui...");
        
        LocalDate today = LocalDate.now();
        List<Tour> tours = tourRepository.findByScheduledDateAndStatus(today, TourStatus.SCHEDULED);
        
        for (Tour tour : tours) {
            try {
                // Marquer le tour comme actif
                tour.setStatus(TourStatus.IN_PROGRESS);
                tour.setStartDate(today);
                tourRepository.save(tour);
                
                TontineGroup group = tour.getGroup();
                
                // Notifier tous les membres
                group.getMemberships().forEach(membership -> {
                    try {
                        Map<String, String> variables = new HashMap<>();
                        variables.put("groupe", group.getNom());
                        variables.put("tour", "Tour " + tour.getIndexInGroup());
                        variables.put("beneficiaire", tour.getBeneficiary().getPrenom() + " " + tour.getBeneficiary().getNom());
                        variables.put("montant", String.format("%,.0f", tour.getExpectedAmount()));
                        
                        notificationService.sendNotificationWithTemplate(
                            membership.getPerson().getId(),
                            NotificationType.TOUR_STARTED,
                            NotificationChannel.PUSH,
                            variables
                        );
                    } catch (Exception e) {
                        log.error("Erreur notification tour démarré pour membre {}", membership.getPerson().getId());
                    }
                });
                
                // Notifier le bénéficiaire avec un message spécial
                Map<String, String> beneficiaryVars = new HashMap<>();
                beneficiaryVars.put("groupe", group.getNom());
                beneficiaryVars.put("montant", String.format("%,.0f", tour.getExpectedAmount()));
                beneficiaryVars.put("date", today.toString());
                
                notificationService.sendNotificationWithTemplate(
                    tour.getBeneficiary().getId(),
                    NotificationType.YOUR_TURN_NOW,
                    NotificationChannel.PUSH,
                    beneficiaryVars
                );
                
                // Envoyer aussi par Email au bénéficiaire
                notificationService.sendNotificationWithTemplate(
                    tour.getBeneficiary().getId(),
                    NotificationType.YOUR_TURN_NOW,
                    NotificationChannel.EMAIL,
                    beneficiaryVars
                );
                
                log.info("✅ Notifications tour démarré envoyées pour {}", group.getNom());
            } catch (Exception e) {
                log.error("❌ Erreur notification tour démarré {}: {}", tour.getId(), e.getMessage());
            }
        }
        
        log.info("✅ {} notifications de tours démarrés envoyées", tours.size());
    }

    /**
     * Exécuté tous les jours à 7h00
     * Envoie les notifications de tontines qui commencent bientôt
     */
    @Scheduled(cron = "0 0 7 * * ?")
    public void sendTontineStartingSoonNotifications() {
        log.info("🔔 Envoi des notifications de tontines qui commencent bientôt...");
        
        LocalDate targetDate = LocalDate.now().plusDays(2);
        List<TontineGroup> groups = groupRepository.findByStartDate(targetDate);
        
        for (TontineGroup group : groups) {
            try {
                // Notifier tous les membres
                group.getMemberships().forEach(membership -> {
                    try {
                        Map<String, String> variables = new HashMap<>();
                        variables.put("groupe", group.getNom());
                        variables.put("date", targetDate.toString());
                        
                        notificationService.sendNotificationWithTemplate(
                            membership.getPerson().getId(),
                            NotificationType.TONTINE_STARTING_SOON,
                            NotificationChannel.PUSH,
                            variables
                        );
                    } catch (Exception e) {
                        log.error("Erreur notification tontine bientôt pour membre {}", membership.getPerson().getId());
                    }
                });
                
                log.info("✅ Notifications tontine bientôt envoyées pour {}", group.getNom());
            } catch (Exception e) {
                log.error("❌ Erreur notification tontine bientôt {}: {}", group.getId(), e.getMessage());
            }
        }
        
        log.info("✅ {} notifications de tontines bientôt envoyées", groups.size());
    }

    /**
     * Exécuté tous les jours à 8h00
     * Envoie les notifications de tontines qui démarrent aujourd'hui
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendTontineStartedNotifications() {
        log.info("🔔 Envoi des notifications de tontines qui démarrent aujourd'hui...");
        
        LocalDate today = LocalDate.now();
        List<TontineGroup> groups = groupRepository.findByStartDate(today);
        
        for (TontineGroup group : groups) {
            try {
                // Notifier tous les membres
                group.getMemberships().forEach(membership -> {
                    try {
                        Map<String, String> variables = new HashMap<>();
                        variables.put("groupe", group.getNom());
                        
                        notificationService.sendNotificationWithTemplate(
                            membership.getPerson().getId(),
                            NotificationType.TONTINE_STARTED,
                            NotificationChannel.PUSH,
                            variables
                        );
                        
                        // Envoyer aussi par Email
                        notificationService.sendNotificationWithTemplate(
                            membership.getPerson().getId(),
                            NotificationType.TONTINE_STARTED,
                            NotificationChannel.EMAIL,
                            variables
                        );
                    } catch (Exception e) {
                        log.error("Erreur notification tontine démarrée pour membre {}", membership.getPerson().getId());
                    }
                });
                
                log.info("✅ Notifications tontine démarrée envoyées pour {}", group.getNom());
            } catch (Exception e) {
                log.error("❌ Erreur notification tontine démarrée {}: {}", group.getId(), e.getMessage());
            }
        }
        
        log.info("✅ {} notifications de tontines démarrées envoyées", groups.size());
    }
}
