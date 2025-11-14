package com.example.pariba.services.impl;

import com.example.pariba.constants.AppConstants;
import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.PayoutRequest;
import com.example.pariba.dtos.responses.PayoutResponse;
import com.example.pariba.enums.PaymentStatus;
import com.example.pariba.enums.TourStatus;
import com.example.pariba.exceptions.BadRequestException;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.Payout;
import com.example.pariba.models.Tour;
import com.example.pariba.repositories.PayoutRepository;
import com.example.pariba.repositories.TourRepository;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.IPayoutService;
import com.example.pariba.services.ITontineGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayoutServiceImpl implements IPayoutService {

    private final PayoutRepository payoutRepository;
    private final TourRepository tourRepository;
    private final ITontineGroupService groupService;
    private final IAuditService auditService;

    public PayoutServiceImpl(PayoutRepository payoutRepository,
                            TourRepository tourRepository,
                            ITontineGroupService groupService,
                            IAuditService auditService) {
        this.payoutRepository = payoutRepository;
        this.tourRepository = tourRepository;
        this.groupService = groupService;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public PayoutResponse processPayout(String personId, PayoutRequest request) {
        Tour tour = tourRepository.findById(request.getTourId())
                .orElseThrow(() -> new ResourceNotFoundException("Tour", "id", request.getTourId()));

        // Vérifier que la personne est admin
        groupService.checkIsAdmin(tour.getGroup().getId(), personId);

        // Vérifier que le tour est complété
        if (tour.getStatus() != TourStatus.IN_PROGRESS && tour.getStatus() != TourStatus.CLOSED) {
            throw new BadRequestException(MessageConstants.PAYOUT_ERROR_TOUR_NOT_COMPLETED);
        }

        // Vérifier qu'il n'y a pas déjà un payout
        if (payoutRepository.existsByTourId(tour.getId())) {
            throw new BadRequestException(MessageConstants.PAYOUT_ERROR_ALREADY_PROCESSED);
        }

        // Créer le payout
        Payout payout = new Payout();
        payout.setTour(tour);
        payout.setBeneficiary(tour.getBeneficiary());
        payout.setAmount(tour.getTotalCollected());
        payout.setPaymentType(request.getPaymentType());
        payout.setStatus(PaymentStatus.PENDING);
        payout.setExternalRef(request.getExternalRef());

        // TODO: Intégrer avec Orange Money / Moov Money API
        // Pour l'instant, on marque comme confirmé directement
        payout.setStatus(PaymentStatus.CONFIRMED);

        payout = payoutRepository.save(payout);

        // Audit log
        auditService.log(personId, AppConstants.AUDIT_PAYOUT_TOUR, "Payout", payout.getId(), 
            String.format("{\"amount\": %s, \"beneficiary\": \"%s\"}", 
                payout.getAmount(), payout.getBeneficiary().getId()));

        return new PayoutResponse(payout);
    }

    @Override
    @Transactional(readOnly = true)
    public PayoutResponse getPayoutById(String payoutId) {
        Payout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Payout", "id", payoutId));
        return new PayoutResponse(payout);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayoutResponse> getPayoutsByTour(String tourId) {
        return payoutRepository.findByTourId(tourId)
                .stream()
                .map(PayoutResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayoutResponse> getPayoutsByBeneficiary(String personId) {
        return payoutRepository.findByBeneficiaryId(personId)
                .stream()
                .map(PayoutResponse::new)
                .collect(Collectors.toList());
    }
}
