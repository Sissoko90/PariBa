package com.example.pariba.repositories;

import com.example.pariba.enums.PaymentStatus;
import com.example.pariba.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    
    List<Payment> findByGroupId(String groupId);
    
    List<Payment> findByPayerId(String payerId);
    
    List<Payment> findByGroupIdAndPayerId(String groupId, String payerId);
    
    List<Payment> findByGroupIdAndStatus(String groupId, PaymentStatus status);
    
    List<Payment> findByGroupIdAndPayout(String groupId, boolean payout);
    
    Optional<Payment> findByExternalRef(String externalRef);
    
    @Query("SELECT p FROM Payment p WHERE p.group.id = :groupId ORDER BY p.createdAt DESC")
    List<Payment> findByGroupIdOrderByCreatedAtDesc(@Param("groupId") String groupId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.group.id = :groupId AND p.status = 'SUCCESS' AND p.payout = false")
    Double getTotalCollectedByGroup(@Param("groupId") String groupId);
    
    List<Payment> findByContributionId(String contributionId);
}
