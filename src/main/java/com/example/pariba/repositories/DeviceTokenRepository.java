package com.example.pariba.repositories;

import com.example.pariba.models.DeviceToken;
import com.example.pariba.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, String> {
    
    List<DeviceToken> findByPersonId(String personId);
    
    List<DeviceToken> findByPersonIdAndActiveTrue(String personId);
    
    List<DeviceToken> findByPersonAndActiveTrue(Person person);
    
    Optional<DeviceToken> findByToken(String token);
    
    Optional<DeviceToken> findByTokenAndPerson(String token, Person person);
    
    List<DeviceToken> findByActiveFalseAndLastUsedAtBefore(LocalDateTime date);
    
    boolean existsByToken(String token);
    
    void deleteByToken(String token);
    
    // Nouvelles méthodes pour la gestion mobile
    List<DeviceToken> findByPerson(Person person);
    
    Optional<DeviceToken> findByIdAndPerson(String id, Person person);
}
