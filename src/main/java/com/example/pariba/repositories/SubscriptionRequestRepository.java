package com.example.pariba.repositories;

import com.example.pariba.enums.SubscriptionRequestStatus;
import com.example.pariba.models.Person;
import com.example.pariba.models.SubscriptionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRequestRepository extends JpaRepository<SubscriptionRequest, String> {
    
    List<SubscriptionRequest> findByPersonId(String personId);
    
    List<SubscriptionRequest> findByPersonIdOrderByCreatedAtDesc(String personId);
    
    Optional<SubscriptionRequest> findByPersonAndStatus(Person person, SubscriptionRequestStatus status);
    
    List<SubscriptionRequest> findByStatus(SubscriptionRequestStatus status);
    
    Page<SubscriptionRequest> findByStatus(SubscriptionRequestStatus status, Pageable pageable);
    
    Page<SubscriptionRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    long countByStatus(SubscriptionRequestStatus status);
    
    boolean existsByPersonAndStatus(Person person, SubscriptionRequestStatus status);
}
