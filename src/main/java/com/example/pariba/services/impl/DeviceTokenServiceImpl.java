package com.example.pariba.services.impl;

import com.example.pariba.dtos.requests.RegisterDeviceRequest;
import com.example.pariba.dtos.responses.DeviceTokenResponse;
import com.example.pariba.dtos.responses.DeviceResponse;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.DeviceToken;
import com.example.pariba.models.Person;
import com.example.pariba.repositories.DeviceTokenRepository;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.services.IDeviceTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des tokens d'appareil
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceTokenServiceImpl implements IDeviceTokenService {
    
    private final DeviceTokenRepository deviceTokenRepository;
    private final PersonRepository personRepository;
    
    @Override
    @Transactional
    public DeviceTokenResponse registerDeviceToken(String personId, RegisterDeviceRequest request) {
        log.info("Enregistrement du token pour person: {}", personId);
        
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée"));
        
        // Vérifier si le token existe déjà
        Optional<DeviceToken> existingToken = deviceTokenRepository
                .findByTokenAndPerson(request.getToken(), person);
        
        DeviceToken deviceToken;
        if (existingToken.isPresent()) {
            // Mettre à jour le token existant
            deviceToken = existingToken.get();
            deviceToken.setActive(true);
            deviceToken.setLastUsedAt(LocalDateTime.now());
            log.info("Token existant mis à jour");
        } else {
            // Créer un nouveau token
            deviceToken = new DeviceToken();
            deviceToken.setToken(request.getToken());
            deviceToken.setPlatform(request.getPlatform());
            deviceToken.setPerson(person);
            deviceToken.setActive(true);
            deviceToken.setLastUsedAt(LocalDateTime.now());
            log.info("Nouveau token créé");
        }
        
        deviceToken = deviceTokenRepository.save(deviceToken);
        return mapToResponse(deviceToken);
    }
    
    @Override
    public List<DeviceTokenResponse> getActiveTokensByPerson(String personId) {
        log.info("Récupération des tokens actifs pour person: {}", personId);
        
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée"));
        
        List<DeviceToken> tokens = deviceTokenRepository.findByPersonAndActiveTrue(person);
        
        return tokens.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deactivateToken(String tokenId, String personId) {
        log.info("Désactivation du token: {} pour person: {}", tokenId, personId);
        
        DeviceToken token = deviceTokenRepository.findById(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException("Token non trouvé"));
        
        // Vérifier que le token appartient bien à la personne
        if (!token.getPerson().getId().equals(personId)) {
            throw new IllegalArgumentException("Ce token n'appartient pas à cette personne");
        }
        
        token.setActive(false);
        deviceTokenRepository.save(token);
    }
    
    @Override
    @Transactional
    public void cleanupInactiveTokens() {
        log.info("Nettoyage des tokens inactifs");
        
        // Supprimer les tokens inactifs depuis plus de 90 jours
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);
        List<DeviceToken> inactiveTokens = deviceTokenRepository
                .findByActiveFalseAndLastUsedAtBefore(cutoffDate);
        
        deviceTokenRepository.deleteAll(inactiveTokens);
        log.info("{} tokens inactifs supprimés", inactiveTokens.size());
    }
    
    // Nouvelles méthodes pour la gestion mobile
    
    @Override
    @Transactional
    public DeviceResponse registerDevice(String personId, RegisterDeviceRequest request) {
        log.info("Enregistrement d'un nouvel appareil pour person: {}", personId);
        
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée"));
        
        // Vérifier si le token existe déjà
        Optional<DeviceToken> existingToken = deviceTokenRepository
                .findByTokenAndPerson(request.getToken(), person);
        
        DeviceToken deviceToken;
        if (existingToken.isPresent()) {
            // Mettre à jour l'appareil existant
            deviceToken = existingToken.get();
            deviceToken.setActive(true);
            deviceToken.setDeviceName(request.getDeviceName());
            deviceToken.setAppVersion(request.getAppVersion());
            deviceToken.setOsVersion(request.getOsVersion());
            deviceToken.setLastUsedAt(LocalDateTime.now());
            log.info("Appareil existant mis à jour");
        } else {
            // Créer un nouvel appareil
            deviceToken = new DeviceToken();
            deviceToken.setPerson(person);
            deviceToken.setToken(request.getToken());
            deviceToken.setPlatform(request.getPlatform());
            deviceToken.setDeviceName(request.getDeviceName());
            deviceToken.setAppVersion(request.getAppVersion());
            deviceToken.setOsVersion(request.getOsVersion());
            deviceToken.setActive(true);
            deviceToken.setLastUsedAt(LocalDateTime.now());
            log.info("Nouvel appareil créé");
        }
        
        deviceToken = deviceTokenRepository.save(deviceToken);
        return new DeviceResponse(deviceToken);
    }

    @Override
    public List<DeviceResponse> getDevicesByPerson(String personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée"));
        
        return deviceTokenRepository.findByPerson(person)
                .stream()
                .map(DeviceResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DeviceResponse activateDevice(String personId, String deviceId) {
        DeviceToken device = findDeviceByPersonAndId(personId, deviceId);
        device.setActive(true);
        device.setLastUsedAt(LocalDateTime.now());
        device = deviceTokenRepository.save(device);
        
        log.info("Appareil {} activé pour person {}", deviceId, personId);
        return new DeviceResponse(device);
    }

    @Override
    @Transactional
    public DeviceResponse deactivateDevice(String personId, String deviceId) {
        DeviceToken device = findDeviceByPersonAndId(personId, deviceId);
        device.setActive(false);
        device = deviceTokenRepository.save(device);
        
        log.info("Appareil {} désactivé pour person {}", deviceId, personId);
        return new DeviceResponse(device);
    }

    @Override
    @Transactional
    public void deleteDevice(String personId, String deviceId) {
        DeviceToken device = findDeviceByPersonAndId(personId, deviceId);
        deviceTokenRepository.delete(device);
        
        log.info("Appareil {} supprimé pour person {}", deviceId, personId);
    }

    @Override
    @Transactional
    public DeviceResponse updateToken(String personId, String deviceId, String newToken) {
        DeviceToken device = findDeviceByPersonAndId(personId, deviceId);
        device.setToken(newToken);
        device.setLastUsedAt(LocalDateTime.now());
        device = deviceTokenRepository.save(device);
        
        log.info("Token mis à jour pour appareil {} de person {}", deviceId, personId);
        return new DeviceResponse(device);
    }

    /**
     * Méthode utilitaire pour trouver un appareil par person et ID
     */
    private DeviceToken findDeviceByPersonAndId(String personId, String deviceId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée"));
        
        return deviceTokenRepository.findByIdAndPerson(deviceId, person)
                .orElseThrow(() -> new ResourceNotFoundException("Appareil non trouvé"));
    }
    
    /**
     * Convertit une entité DeviceToken en DeviceTokenResponse
     */
    private DeviceTokenResponse mapToResponse(DeviceToken token) {
        return DeviceTokenResponse.builder()
                .id(token.getId())
                .token(token.getToken())
                .platform(token.getPlatform())
                .personId(token.getPerson().getId())
                .active(token.getActive())
                .createdAt(token.getCreatedAt() != null ? java.time.LocalDateTime.ofInstant(token.getCreatedAt(), java.time.ZoneId.systemDefault()) : null)
                .lastUsedAt(token.getLastUsedAt())
                .build();
    }
}
