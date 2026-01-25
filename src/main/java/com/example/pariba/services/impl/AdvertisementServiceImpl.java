package com.example.pariba.services.impl;

import com.example.pariba.dtos.responses.AdvertisementResponse;
import com.example.pariba.enums.AdEventType;
import com.example.pariba.enums.AdPlacement;
import com.example.pariba.enums.SubscriptionStatus;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.AdEvent;
import com.example.pariba.models.Advertisement;
import com.example.pariba.models.Person;
import com.example.pariba.models.Subscription;
import com.example.pariba.repositories.AdEventRepository;
import com.example.pariba.repositories.AdvertisementRepository;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.repositories.SubscriptionRepository;
import com.example.pariba.services.IAdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des publicités
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertisementServiceImpl implements IAdvertisementService {
    
    private final AdvertisementRepository advertisementRepository;
    private final AdEventRepository adEventRepository;
    private final PersonRepository personRepository;
    private final SubscriptionRepository subscriptionRepository;
    
    @Override
    public List<AdvertisementResponse> getActiveAdvertisements(String placement, String personId) {
        log.info("Récupération des publicités actives pour placement: {}", placement);
        
        // Vérifier si l'utilisateur a un abonnement actif
        if (hasActiveSubscription(personId)) {
            log.info("Utilisateur {} a un abonnement actif - Pas de publicités", personId);
            return Collections.emptyList();
        }
        
        LocalDateTime now = LocalDateTime.now();
        List<Advertisement> ads;
        
        // Si placement est null, récupérer toutes les publicités actives
        if (placement == null || placement.isEmpty()) {
            log.info("Récupération de toutes les publicités actives");
            ads = advertisementRepository.findByActiveTrue();
        } else {
            log.info("Récupération des publicités pour placement: {}", placement);
            try {
                AdPlacement adPlacement = AdPlacement.valueOf(placement);
                ads = advertisementRepository.findByPlacementAndActiveTrue(adPlacement);
            } catch (IllegalArgumentException e) {
                log.error("Placement invalide: {}", placement);
                return Collections.emptyList();
            }
        }
        
        // Filtrer les publicités selon les dates de validité
        return ads.stream()
                .filter(ad -> (ad.getStartDate() == null || ad.getStartDate().isBefore(now)) &&
                             (ad.getEndDate() == null || ad.getEndDate().isAfter(now)))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Vérifie si l'utilisateur a un abonnement actif (payant)
     */
    private boolean hasActiveSubscription(String personId) {
        try {
            // Si personId est null (utilisateur non authentifié), pas d'abonnement
            if (personId == null || personId.isEmpty()) {
                log.info("PersonId null - Utilisateur non authentifié, pas d'abonnement");
                return false;
            }
            
            Person person = personRepository.findById(personId)
                    .orElse(null);
            
            if (person == null) {
                return false;
            }
            
            Optional<Subscription> subscription = subscriptionRepository
                    .findByPersonAndStatus(person, SubscriptionStatus.ACTIVE);
            
            // Si l'utilisateur a un abonnement actif et que ce n'est pas le plan FREE
            if (subscription.isPresent()) {
                String planType = subscription.get().getPlan().getType().name();
                return !planType.equals("FREE");
            }
            
            return false;
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de l'abonnement: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    @Transactional
    public void recordImpression(String adId, String personId) {
        log.info("Enregistrement d'une impression pour ad: {}, person: {}", adId, personId);
        
        // Vérifier que la publicité existe
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new ResourceNotFoundException("Publicité non trouvée"));
        
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée"));
        
        // Créer un événement d'impression
        AdEvent event = new AdEvent();
        event.setAd(ad);
        event.setPerson(person);
        event.setEventType(AdEventType.IMPRESSION);
        event.setEventTimestamp(LocalDateTime.now());
        
        adEventRepository.save(event);
        
        // Incrémenter atomiquement le compteur d'impressions (évite les deadlocks)
        advertisementRepository.incrementImpressions(adId);
    }
    
    @Override
    @Transactional
    public void recordClick(String adId, String personId) {
        log.info("Enregistrement d'un clic pour ad: {}, person: {}", adId, personId);
        
        // Vérifier que la publicité existe
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new ResourceNotFoundException("Publicité non trouvée"));
        
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée"));
        
        // Créer un événement de clic
        AdEvent event = new AdEvent();
        event.setAd(ad);
        event.setPerson(person);
        event.setEventType(AdEventType.CLICK);
        event.setEventTimestamp(LocalDateTime.now());
        
        adEventRepository.save(event);
        
        // Incrémenter atomiquement le compteur de clics (évite les deadlocks)
        advertisementRepository.incrementClicks(adId);
    }
    
    @Override
    public AdvertisementResponse getAdvertisementById(String adId) {
        log.info("Récupération de la publicité: {}", adId);
        
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new ResourceNotFoundException("Publicité non trouvée"));
        
        return mapToResponse(ad);
    }
    
    /**
     * Convertit une entité Advertisement en AdvertisementResponse
     */
    private AdvertisementResponse mapToResponse(Advertisement ad) {
        return AdvertisementResponse.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .imageUrl(ad.getImageUrl())
                .linkUrl(ad.getLinkUrl())
                .videoUrl(ad.getVideoUrl())
                .placement(ad.getPlacement().name())
                .targetingCriteria(ad.getTargetingCriteria())
                .active(ad.getActive())
                .impressions(ad.getImpressions() != null ? ad.getImpressions().longValue() : 0L)
                .clicks(ad.getClicks() != null ? ad.getClicks().longValue() : 0L)
                .startDate(ad.getStartDate())
                .endDate(ad.getEndDate())
                .createdAt(ad.getCreatedAt() != null ? LocalDateTime.ofInstant(ad.getCreatedAt(), java.time.ZoneId.systemDefault()) : null)
                .build();
    }
}
