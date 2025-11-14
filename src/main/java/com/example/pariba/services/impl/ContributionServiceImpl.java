package com.example.pariba.services.impl;

import com.example.pariba.dtos.responses.ContributionResponse;
import com.example.pariba.enums.ContributionStatus;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.Contribution;
import com.example.pariba.repositories.ContributionRepository;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.IContributionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContributionServiceImpl implements IContributionService {

    private final ContributionRepository contributionRepository;
    private final IAuditService auditService;

    public ContributionServiceImpl(ContributionRepository contributionRepository,
                                  IAuditService auditService) {
        this.contributionRepository = contributionRepository;
        this.auditService = auditService;
    }

    @Override
    @Transactional(readOnly = true)
    public ContributionResponse getContributionById(String contributionId) {
        Contribution contribution = contributionRepository.findById(contributionId)
                .orElseThrow(() -> new ResourceNotFoundException("Contribution", "id", contributionId));
        return new ContributionResponse(contribution);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContributionResponse> getContributionsByTour(String tourId) {
        return contributionRepository.findByTourId(tourId)
                .stream()
                .map(ContributionResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContributionResponse> getContributionsByMember(String personId) {
        return contributionRepository.findByMemberId(personId)
                .stream()
                .map(ContributionResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContributionResponse> getPendingContributions(String groupId) {
        return contributionRepository.findPendingContributionsByGroup(groupId)
                .stream()
                .map(ContributionResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void applyLatePenalties() {
        LocalDate today = LocalDate.now();
        List<Contribution> lateContributions = contributionRepository.findLateContributions(today);

        for (Contribution contribution : lateContributions) {
            if (contribution.getTour().getGroup().getLatePenaltyAmount() != null) {
                // Calculer les jours de retard après la période de grâce
                int graceDays = contribution.getTour().getGroup().getGraceDays();
                long daysLate = java.time.temporal.ChronoUnit.DAYS.between(
                    contribution.getDueDate().plusDays(graceDays), 
                    today
                );

                if (daysLate > 0) {
                    BigDecimal penalty = contribution.getTour().getGroup().getLatePenaltyAmount()
                            .multiply(BigDecimal.valueOf(daysLate));
                    contribution.setPenaltyApplied(penalty);
                    contributionRepository.save(contribution);

                    auditService.log(null, "APPLY_PENALTY", "Contribution", contribution.getId(), 
                        String.format("{\"penalty\": %s, \"daysLate\": %d}", penalty, daysLate));
                }
            }
        }
    }

    @Override
    @Transactional
    public void markAsPaid(String contributionId) {
        Contribution contribution = contributionRepository.findById(contributionId)
                .orElseThrow(() -> new ResourceNotFoundException("Contribution", "id", contributionId));

        contribution.setStatus(ContributionStatus.PAID);
        contributionRepository.save(contribution);
    }
}
