package com.example.pariba.repositories;

import com.example.pariba.enums.AppRole;
import com.example.pariba.models.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, String> {
    
    Optional<Person> findByEmail(String email);
    
    Optional<Person> findByPhone(String phone);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    long countByRole(AppRole role);
    
    long countByCreatedAtBetween(Instant start, Instant end);
    
    long countByCreatedAtAfter(LocalDateTime date);
    
    // Recherche avancée
    Page<Person> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContaining(
        String nom, String prenom, String email, String phone, Pageable pageable);
}
