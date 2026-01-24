package com.example.pariba.services.impl;

import com.example.pariba.dtos.responses.SubscriptionResponse;
import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.enums.SubscriptionStatus;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.Person;
import com.example.pariba.models.Subscription;
import com.example.pariba.models.SubscriptionPlan;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.repositories.SubscriptionPlanRepository;
import com.example.pariba.repositories.SubscriptionRepository;
import com.example.pariba.services.INotificationService;
import com.example.pariba.services.ISubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implémentation du service de gestion des abonnements
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements ISubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PersonRepository personRepository;
    private final INotificationService notificationService;
    
    @Override
    public SubscriptionResponse getActiveSubscription(String personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée"));
        
        Optional<Subscription> subscription = subscriptionRepository
                .findByPersonAndStatus(person, SubscriptionStatus.ACTIVE);
        
        return subscription.map(this::mapToResponse).orElse(null);
    }
    
    @Override
    @Transactional
    public SubscriptionResponse subscribe(String personId, String planId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée"));
        
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan non trouvé"));
        
        // Vérifier s'il existe déjà un abonnement actif
        Optional<Subscription> existingSubscription = subscriptionRepository
                .findByPersonAndStatus(person, SubscriptionStatus.ACTIVE);
        
        Subscription subscription;
        if (existingSubscription.isPresent()) {
            // Mettre à niveau l'abonnement existant
            subscription = existingSubscription.get();
            subscription.setPlan(plan);
            subscription.setStartDate(LocalDate.now());
            subscription.setEndDate(calculateEndDate(plan));
            // Abonnement existant mis à niveau
        } else {
            // Créer un nouvel abonnement
            subscription = new Subscription();
            subscription.setPerson(person);
            subscription.setPlan(plan);
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setStartDate(LocalDate.now());
            subscription.setEndDate(calculateEndDate(plan));
            subscription.setAutoRenew(true);
            // Nouvel abonnement créé
        }
        
        subscription = subscriptionRepository.save(subscription);
        
        // Envoyer notification d'abonnement
        try {
            Map<String, String> variables = new HashMap<>();
            variables.put("plan", plan.getName());
            variables.put("montant", String.format("%,.0f", plan.getMonthlyPrice()));
            variables.put("date_fin", subscription.getEndDate().toString());
            
            if (existingSubscription.isPresent()) {
                // Mise à niveau
                notificationService.sendNotificationWithTemplate(
                    personId,
                    NotificationType.SYSTEM_UPDATE,
                    NotificationChannel.PUSH,
                    variables
                );
                log.info("✅ Notification mise à niveau abonnement envoyée à {}", personId);
            } else {
                // Nouvel abonnement
                notificationService.sendNotificationWithTemplate(
                    personId,
                    NotificationType.SYSTEM_UPDATE,
                    NotificationChannel.PUSH,
                    variables
                );
                
                // Envoyer aussi par Email
                notificationService.sendNotificationWithTemplate(
                    personId,
                    NotificationType.SYSTEM_UPDATE,
                    NotificationChannel.EMAIL,
                    variables
                );
                log.info("✅ Notification nouvel abonnement envoyée à {}", personId);
            }
        } catch (Exception e) {
            log.error("❌ Erreur notification abonnement: {}", e.getMessage());
        }
        
        return mapToResponse(subscription);
    }
    
    @Override
    @Transactional
    public void cancelSubscription(String personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée"));
        
        Optional<Subscription> subscription = subscriptionRepository
                .findByPersonAndStatus(person, SubscriptionStatus.ACTIVE);
        
        if (subscription.isPresent()) {
            Subscription sub = subscription.get();
            sub.setStatus(SubscriptionStatus.CANCELLED);
            sub.setAutoRenew(false);
            subscriptionRepository.save(sub);
            
            // Envoyer notification d'annulation
            try {
                Map<String, String> variables = new HashMap<>();
                variables.put("plan", sub.getPlan().getName());
                variables.put("date_fin", sub.getEndDate().toString());
                
                notificationService.sendNotificationWithTemplate(
                    personId,
                    NotificationType.SYSTEM_UPDATE,
                    NotificationChannel.PUSH,
                    variables
                );
                
                notificationService.sendNotificationWithTemplate(
                    personId,
                    NotificationType.SYSTEM_UPDATE,
                    NotificationChannel.EMAIL,
                    variables
                );
                
                log.info("✅ Notification annulation abonnement envoyée à {}", personId);
            } catch (Exception e) {
                log.error("❌ Erreur notification annulation: {}", e.getMessage());
            }
        } else {
            throw new ResourceNotFoundException("Aucun abonnement actif trouvé");
        }
    }
    
    @Override
    public boolean hasFeatureAccess(String personId, String feature) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée"));
        
        Optional<Subscription> subscription = subscriptionRepository
                .findByPersonAndStatus(person, SubscriptionStatus.ACTIVE);
        
        if (subscription.isEmpty()) {
            // Pas d'abonnement actif, vérifier les fonctionnalités gratuites
            return isFreePlanFeature(feature);
        }
        
        // Vérifier si le plan inclut la fonctionnalité
        SubscriptionPlan plan = subscription.get().getPlan();
        String features = plan.getFeatures();
        
        return features != null && features.contains(feature);
    }
    
    @Override
    @Transactional
    public void renewExpiredSubscriptions() {
        LocalDate now = LocalDate.now();
        List<Subscription> expiredSubscriptions = subscriptionRepository
                .findByStatusAndEndDateBeforeAndAutoRenewTrue(SubscriptionStatus.ACTIVE, now);
        
        for (Subscription subscription : expiredSubscriptions) {
            if (subscription.getAutoRenew()) {
                // Renouveler l'abonnement
                subscription.setStartDate(now);
                subscription.setEndDate(calculateEndDate(subscription.getPlan()));
                subscriptionRepository.save(subscription);
                
                // Notification de renouvellement
                try {
                    Map<String, String> variables = new HashMap<>();
                    variables.put("plan", subscription.getPlan().getName());
                    variables.put("montant", String.format("%,.0f", subscription.getPlan().getMonthlyPrice()));
                    variables.put("date_fin", subscription.getEndDate().toString());
                    
                    notificationService.sendNotificationWithTemplate(
                        subscription.getPerson().getId(),
                        NotificationType.SYSTEM_UPDATE,
                        NotificationChannel.PUSH,
                        variables
                    );
                    
                    notificationService.sendNotificationWithTemplate(
                        subscription.getPerson().getId(),
                        NotificationType.SYSTEM_UPDATE,
                        NotificationChannel.EMAIL,
                        variables
                    );
                    
                    log.info("✅ Notification renouvellement envoyée");
                } catch (Exception e) {
                    log.error("❌ Erreur notification renouvellement: {}", e.getMessage());
                }
            } else {
                // Marquer comme expiré
                subscription.setStatus(SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(subscription);
                
                // Notification d'expiration
                try {
                    Map<String, String> variables = new HashMap<>();
                    variables.put("plan", subscription.getPlan().getName());
                    variables.put("date", now.toString());
                    
                    notificationService.sendNotificationWithTemplate(
                        subscription.getPerson().getId(),
                        NotificationType.SYSTEM_UPDATE,
                        NotificationChannel.PUSH,
                        variables
                    );
                    
                    notificationService.sendNotificationWithTemplate(
                        subscription.getPerson().getId(),
                        NotificationType.SYSTEM_UPDATE,
                        NotificationChannel.EMAIL,
                        variables
                    );
                    
                    log.info("✅ Notification expiration envoyée");
                } catch (Exception e) {
                    log.error("❌ Erreur notification expiration: {}", e.getMessage());
                }
            }
        }
    }
    
    /**
     * Calcule la date de fin d'un abonnement basé sur le plan
     */
    private LocalDate calculateEndDate(SubscriptionPlan plan) {
        LocalDate now = LocalDate.now();
        
        // Par défaut, un abonnement dure 30 jours
        // Vous pouvez ajouter une logique plus complexe basée sur le plan
        return now.plusMonths(1);
    }
    
    /**
     * Vérifie si une fonctionnalité est disponible dans le plan gratuit
     */
    private boolean isFreePlanFeature(String feature) {
        // Liste des fonctionnalités gratuites
        return feature.equals("basic_groups") || 
               feature.equals("basic_notifications") ||
               feature.equals("basic_payments");
    }
    
    /**
     * Convertit une entité Subscription en SubscriptionResponse
     */
    private SubscriptionResponse mapToResponse(Subscription subscription) {
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .planType(subscription.getPlan().getType())
                .planName(subscription.getPlan().getName())
                .monthlyPrice(subscription.getPlan().getMonthlyPrice())
                .status(subscription.getStatus())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .autoRenew(subscription.getAutoRenew())
                .featuresJson(subscription.getPlan().getFeaturesJson())
                .createdAt(subscription.getCreatedAt())
                .build();
    }
}
