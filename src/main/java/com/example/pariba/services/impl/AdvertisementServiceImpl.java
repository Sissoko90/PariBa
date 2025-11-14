package com.example.pariba.services.impl;

import com.example.pariba.dtos.responses.AdvertisementResponse;
import com.example.pariba.enums.AdEventType;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.AdEvent;
import com.example.pariba.models.Advertisement;
import com.example.pariba.models.Person;
import com.example.pariba.repositories.AdEventRepository;
import com.example.pariba.repositories.AdvertisementRepository;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.services.IAdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    
    @Override
    public List<AdvertisementResponse> getActiveAdvertisements(String placement, String personId) {
        log.info("Récupération des publicités actives pour placement: {}", placement);
        
        LocalDateTime now = LocalDateTime.now();
        List<Advertisement> ads = advertisementRepository.findByPlacementAndActiveTrue(placement);
        
        // Filtrer les publicités selon les dates de validité
        return ads.stream()
                .filter(ad -> (ad.getStartDate() == null || ad.getStartDate().isBefore(now)) &&
                             (ad.getEndDate() == null || ad.getEndDate().isAfter(now)))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void recordImpression(String adId, String personId) {
        log.info("Enregistrement d'une impression pour ad: {}, person: {}", adId, personId);
        
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
        
        // Incrémenter le compteur d'impressions
        ad.setImpressions(ad.getImpressions() + 1);
        advertisementRepository.save(ad);
    }
    
    @Override
    @Transactional
    public void recordClick(String adId, String personId) {
        log.info("Enregistrement d'un clic pour ad: {}, person: {}", adId, personId);
        
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
        
        // Incrémenter le compteur de clics
        ad.setClicks(ad.getClicks() + 1);
        advertisementRepository.save(ad);
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
