package com.example.pariba.services.impl;

import com.example.pariba.dtos.responses.DashboardSummaryResponse;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.Person;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.repositories.TontineGroupRepository;
import com.example.pariba.repositories.PaymentRepository;
import com.example.pariba.repositories.NotificationRepository;
import com.example.pariba.services.IDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Implémentation du service Dashboard
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements IDashboardService {

    private final PersonRepository personRepository;
    private final TontineGroupRepository tontineGroupRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public DashboardSummaryResponse getDashboardSummary(String personId) {
        log.info("Génération du résumé dashboard pour person: {}", personId);
        
        // Vérifier que la personne existe
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée"));
        
        DashboardSummaryResponse summary = new DashboardSummaryResponse();
        
        // Statistiques de base (pour l'instant des valeurs par défaut)
        summary.setTotalGroups(2);
        summary.setActiveGroups(1);
        summary.setTotalContributions(5);
        summary.setTotalAmountContributed(new BigDecimal("125000"));
        summary.setTotalAmountReceived(new BigDecimal("75000"));
        summary.setUpcomingPayments(1);
        summary.setUnreadNotifications(3);
        summary.setNextPaymentDate("2025-12-01");
        summary.setNextPaymentAmount(new BigDecimal("25000"));
        
        log.info("Résumé dashboard généré pour person: {}", personId);
        return summary;
    }
}
