package com.example.pariba.repositories;

import com.example.pariba.enums.SubscriptionPlanType;
import com.example.pariba.models.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, String> {
    
    Optional<SubscriptionPlan> findByType(SubscriptionPlanType type);
    
    Optional<SubscriptionPlan> findByName(String name);
}
