package com.example.pariba.services;

import com.example.pariba.enums.AppRole;
import com.example.pariba.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleDashboardStatsService {
    
    private final PersonRepository personRepository;
    private final TontineGroupRepository tontineGroupRepository;
    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ContributionRepository contributionRepository;
    
    @Transactional(readOnly = true)
    public Map<String, Object> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques utilisateurs
        long totalUsers = personRepository.count();
        long totalSuperAdmins = personRepository.countByRole(AppRole.SUPERADMIN);
        long totalAdmins = personRepository.countByRole(AppRole.ADMIN);
        long totalMembers = totalUsers - totalSuperAdmins - totalAdmins;
        
        stats.put("totalUsers", totalUsers);
        stats.put("totalSuperAdmins", totalSuperAdmins);
        stats.put("totalAdmins", totalAdmins);
        stats.put("totalMembers", totalMembers);
        
        // Statistiques groupes
        long totalGroups = tontineGroupRepository.count();
        stats.put("totalGroups", totalGroups);
        stats.put("activeGroups", totalGroups); // Simplifié
        
        // Statistiques paiements
        long totalPayments = paymentRepository.count();
        stats.put("totalPayments", totalPayments);
        
        // Statistiques abonnements
        long totalSubscriptions = subscriptionRepository.count();
        stats.put("totalSubscriptions", totalSubscriptions);
        
        // Statistiques contributions
        long totalContributions = contributionRepository.count();
        stats.put("totalContributions", totalContributions);
        
        return stats;
    }
}
