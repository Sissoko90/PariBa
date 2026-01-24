package com.example.pariba.repositories;

import com.example.pariba.enums.SubscriptionStatus;
import com.example.pariba.models.Person;
import com.example.pariba.models.Subscription;
import com.example.pariba.models.SubscriptionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    
    Optional<Subscription> findByPersonIdAndStatus(String personId, SubscriptionStatus status);
    
    Optional<Subscription> findByPersonAndStatus(Person person, SubscriptionStatus status);
    
    List<Subscription> findByPersonId(String personId);
    
    List<Subscription> findByStatus(SubscriptionStatus status);
    
    Page<Subscription> findByStatus(SubscriptionStatus status, Pageable pageable);
    
    long countByStatus(SubscriptionStatus status);
    
    long countByPlanAndStatus(SubscriptionPlan plan, SubscriptionStatus status);
    
    long countByCreatedAtAfter(Instant date);
    
    long countByStatusAndEndDateBetween(SubscriptionStatus status, LocalDate startDate, LocalDate endDate);
    
    List<Subscription> findByStatusAndEndDateBeforeAndAutoRenewTrue(SubscriptionStatus status, LocalDate date);
    
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' AND s.endDate < :date")
    List<Subscription> findExpiredSubscriptions(@Param("date") LocalDate date);
    
    @Query("SELECT s FROM Subscription s WHERE s.person.id = :personId ORDER BY s.createdAt DESC")
    List<Subscription> findByPersonIdOrderByCreatedAtDesc(@Param("personId") String personId);
}
