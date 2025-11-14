package com.example.pariba.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Statistiques personnelles d'un utilisateur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalStatisticsResponse {
    
    // Groupes
    private long totalGroups;
    private long activeGroups;
    private long groupsAsAdmin;
    
    // Contributions
    private long totalContributions;
    private BigDecimal totalContributed;
    private BigDecimal averageContribution;
    
    // Paiements
    private long totalPayments;
    private BigDecimal totalPaid;
    private long successfulPayments;
    private long failedPayments;
    
    // Payouts reçus
    private long totalPayoutsReceived;
    private BigDecimal totalAmountReceived;
    
    // Taux de participation
    private double participationRate; // Pourcentage
    
    // Activité
    private long notificationsReceived;
    private long unreadNotifications;
    
    // Classement (optionnel)
    private Integer ranking;
    private String badge;
}
