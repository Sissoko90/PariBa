package com.example.pariba.services.impl;

import com.example.pariba.dtos.requests.UpdateProfileRequest;
import com.example.pariba.dtos.responses.PersonResponse;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.Person;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.services.IPersonService;
import com.example.pariba.storages.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class PersonServiceImpl implements IPersonService {

    private final PersonRepository personRepository;
    private final StorageService storageService;

    public PersonServiceImpl(PersonRepository personRepository, 
                            StorageService storageService) {
        this.personRepository = personRepository;
        this.storageService = storageService;
    }

    @Transactional(readOnly = true)
    public PersonResponse getPersonById(String personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));
        return new PersonResponse(person);
    }

    @Transactional
    public PersonResponse updateProfile(String personId, UpdateProfileRequest request) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));

        if (request.getPrenom() != null) {
            person.setPrenom(request.getPrenom());
        }
        if (request.getNom() != null) {
            person.setNom(request.getNom());
        }
        if (request.getEmail() != null) {
            person.setEmail(request.getEmail());
        }
        if (request.getPhoto() != null) {
            person.setPhoto(request.getPhoto());
        }

        person = personRepository.save(person);
        return new PersonResponse(person);
    }

    @Transactional
    public PersonResponse uploadPhoto(String personId, MultipartFile file) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));

        // Supprimer l'ancienne photo si elle existe
        if (person.getPhoto() != null && !person.getPhoto().isEmpty()) {
            storageService.delete(person.getPhoto());
        }

        // Sauvegarder la nouvelle photo
        String photoUrl = storageService.store(file, "profiles");
        person.setPhoto(photoUrl);
        person = personRepository.save(person);

        return new PersonResponse(person);
    }

    @Override
    @Transactional
    public void deletePhoto(String personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));

        if (person.getPhoto() != null && !person.getPhoto().isEmpty()) {
            // Supprimer le fichier physique
            try {
                storageService.delete(person.getPhoto());
            } catch (Exception e) {
                log.warn("Erreur lors de la suppression de la photo: {}", e.getMessage());
            }
            
            // Supprimer la référence en base
            person.setPhoto(null);
            personRepository.save(person);
            log.info("✅ Photo supprimée pour l'utilisateur: {}", personId);
        }
    }

    @Override
    @Transactional
    public void deleteAccount(String personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));

        // Supprimer la photo de profil si elle existe
        if (person.getPhoto() != null && !person.getPhoto().isEmpty()) {
            try {
                storageService.delete(person.getPhoto());
            } catch (Exception e) {
                log.warn("Erreur lors de la suppression de la photo: {}", e.getMessage());
            }
        }

        // Supprimer le compte
        // Note: Les relations en cascade (groupes, contributions, etc.) seront gérées par JPA
        personRepository.delete(person);
        log.info("✅ Compte supprimé pour l'utilisateur: {}", personId);
    }

    @Override
    @Transactional(readOnly = true)
    public com.example.pariba.dtos.responses.PersonalStatisticsResponse getPersonalStatistics(String personId) {
        // TODO: Implémenter les statistiques personnelles
        // Pour l'instant, retourner des valeurs par défaut
        return new com.example.pariba.dtos.responses.PersonalStatisticsResponse();
    }
}
