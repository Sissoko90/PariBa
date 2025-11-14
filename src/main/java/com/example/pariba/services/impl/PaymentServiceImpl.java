package com.example.pariba.services.impl;

import com.example.pariba.constants.AppConstants;
import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.PaymentRequest;
import com.example.pariba.dtos.responses.PaymentResponse;
import com.example.pariba.enums.ContributionStatus;
import com.example.pariba.enums.PaymentStatus;
import com.example.pariba.exceptions.BadRequestException;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.Contribution;
import com.example.pariba.models.Payment;
import com.example.pariba.repositories.ContributionRepository;
import com.example.pariba.repositories.PaymentRepository;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.IContributionService;
import com.example.pariba.services.IPaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final ContributionRepository contributionRepository;
    private final PersonRepository personRepository;
    private final IContributionService contributionService;
    private final IAuditService auditService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                             ContributionRepository contributionRepository,
                             PersonRepository personRepository,
                             IContributionService contributionService,
                             IAuditService auditService) {
        this.paymentRepository = paymentRepository;
        this.contributionRepository = contributionRepository;
        this.personRepository = personRepository;
        this.contributionService = contributionService;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(String personId, PaymentRequest request) {
        Contribution contribution = contributionRepository.findById(request.getContributionId())
                .orElseThrow(() -> new ResourceNotFoundException("Contribution", "id", request.getContributionId()));

        // Vérifier que la personne est bien le membre de cette contribution
        if (!contribution.getMember().getId().equals(personId)) {
            throw new BadRequestException(MessageConstants.PAYMENT_ERROR_NOT_YOUR_CONTRIBUTION);
        }

        // Vérifier que la contribution n'est pas déjà payée
        if (contribution.getStatus() == ContributionStatus.PAID) {
            throw new BadRequestException(MessageConstants.PAYMENT_ERROR_ALREADY_PAID);
        }

        // Créer le paiement
        Payment payment = new Payment();
        payment.setContribution(contribution);
        payment.setPayer(personRepository.findById(personId).orElseThrow());
        payment.setAmount(request.getAmount());
        payment.setPaymentType(request.getPaymentType());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setExternalRef(request.getExternalRef());

        // TODO: Intégrer avec Orange Money / Moov Money API
        // Pour l'instant, on marque comme confirmé directement
        payment.setStatus(PaymentStatus.CONFIRMED);

        payment = paymentRepository.save(payment);

        // Vérifier si le montant total payé couvre la contribution
        BigDecimal totalPaid = paymentRepository.findByContributionId(contribution.getId())
                .stream()
                .filter(p -> p.getStatus() == PaymentStatus.CONFIRMED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDue = contribution.getAmountDue().add(contribution.getPenaltyApplied());

        if (totalPaid.compareTo(totalDue) >= 0) {
            contributionService.markAsPaid(contribution.getId());
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            contribution.setStatus(ContributionStatus.PARTIAL);
            contributionRepository.save(contribution);
        }

        // Audit log
        auditService.log(personId, AppConstants.AUDIT_PAYMENT, "Payment", payment.getId(), 
            String.format("{\"amount\": %s, \"type\": \"%s\"}", request.getAmount(), request.getPaymentType()));

        return new PaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        return new PaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByContribution(String contributionId) {
        return paymentRepository.findByContributionId(contributionId)
                .stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByPerson(String personId) {
        return paymentRepository.findByPayerId(personId)
                .stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void verifyPayment(String paymentId, String personId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        // TODO: Vérifier avec l'API du provider de paiement
        payment.setStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);

        auditService.log(personId, "VERIFY_PAYMENT", "Payment", paymentId, null);
    }
}
