package com.example.pariba.repositories;

import com.example.pariba.models.SupportContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupportContactRepository extends JpaRepository<SupportContact, String> {
    
    Optional<SupportContact> findFirstByActiveTrue();
    
    Optional<SupportContact> findByEmail(String email);
}
