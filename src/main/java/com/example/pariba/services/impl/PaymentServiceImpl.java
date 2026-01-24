package com.example.pariba.services.impl;

import com.example.pariba.constants.AppConstants;
import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.CashPaymentRequest;
import com.example.pariba.dtos.requests.PaymentRequest;
import com.example.pariba.dtos.responses.PaymentResponse;
import com.example.pariba.enums.ContributionStatus;
import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.enums.PaymentStatus;
import com.example.pariba.enums.PaymentType;
import com.example.pariba.exceptions.BadRequestException;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.Contribution;
import com.example.pariba.models.Payment;
import com.example.pariba.repositories.ContributionRepository;
import com.example.pariba.repositories.PaymentRepository;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.IContributionService;
import com.example.pariba.services.INotificationService;
import com.example.pariba.services.IPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final ContributionRepository contributionRepository;
    private final PersonRepository personRepository;
    private final IContributionService contributionService;
    private final IAuditService auditService;
    private final INotificationService notificationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                             ContributionRepository contributionRepository,
                             PersonRepository personRepository,
                             IContributionService contributionService,
                             IAuditService auditService,
                             INotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.contributionRepository = contributionRepository;
        this.personRepository = personRepository;
        this.contributionService = contributionService;
        this.auditService = auditService;
        this.notificationService = notificationService;
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

        // Envoyer notification de paiement
        try {
            Map<String, String> variables = new HashMap<>();
            variables.put("montant", String.format("%,.0f", payment.getAmount()));
            variables.put("groupe", contribution.getGroup().getNom());
            variables.put("date", LocalDate.now().toString());
            variables.put("reference", payment.getExternalRef() != null ? payment.getExternalRef() : payment.getId());
            
            if (payment.getStatus() == PaymentStatus.CONFIRMED) {
                // Notification de succès
                notificationService.sendNotificationWithTemplate(
                    personId,
                    NotificationType.PAYMENT_SUCCESS,
                    NotificationChannel.PUSH,
                    variables
                );
                
                // Envoyer aussi par Email
                notificationService.sendNotificationWithTemplate(
                    personId,
                    NotificationType.PAYMENT_SUCCESS,
                    NotificationChannel.EMAIL,
                    variables
                );
                
                // Notifier l'admin du groupe de la réception
                notificationService.sendNotificationWithTemplate(
                    contribution.getGroup().getCreator().getId(),
                    NotificationType.CONTRIBUTION_RECEIVED,
                    NotificationChannel.PUSH,
                    variables
                );
                
                log.info("✅ Notifications paiement succès envoyées");
            } else if (payment.getStatus() == PaymentStatus.FAILED) {
                // Notification d'échec
                variables.put("raison", "Erreur lors du traitement");
                variables.put("lien", "https://pariba.app/pay/" + contribution.getId());
                
                notificationService.sendNotificationWithTemplate(
                    personId,
                    NotificationType.PAYMENT_FAILED,
                    NotificationChannel.PUSH,
                    variables
                );
                
                log.info("⚠️ Notification paiement échoué envoyée");
            }
        } catch (Exception e) {
            log.error("❌ Erreur notification paiement: {}", e.getMessage());
        }

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

    @Override
    @Transactional
    public PaymentResponse validateCashPayment(String adminId, CashPaymentRequest request) {
        // Vérifier que l'admin est bien admin du groupe
        Contribution contribution = contributionRepository.findById(request.getContributionId())
                .orElseThrow(() -> new ResourceNotFoundException("Contribution", "id", request.getContributionId()));
        
        // Vérifier que la personne est admin du groupe
        if (!contribution.getGroup().getCreator().getId().equals(adminId)) {
            throw new BadRequestException("Seul l'administrateur du groupe peut valider les paiements cash");
        }
        
        // Vérifier que le membre appartient bien au groupe
        if (!contribution.getMember().getId().equals(request.getMemberId())) {
            throw new BadRequestException("Le membre ne correspond pas à cette contribution");
        }
        
        // Créer le paiement cash
        Payment payment = new Payment();
        payment.setContribution(contribution);
        payment.setPayer(contribution.getMember());
        payment.setAmount(request.getAmount());
        payment.setPaymentType(PaymentType.CASH);
        payment.setStatus(PaymentStatus.CONFIRMED); // Directement confirmé par l'admin
        payment.setExternalRef("CASH-" + System.currentTimeMillis());
        
        payment = paymentRepository.save(payment);
        
        // Mettre à jour le statut de la contribution
        BigDecimal totalPaid = paymentRepository.findByContributionId(contribution.getId())
                .stream()
                .filter(p -> p.getStatus() == PaymentStatus.CONFIRMED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalPaid.compareTo(contribution.getAmountDue()) >= 0) {
            contributionService.markAsPaid(contribution.getId());
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            contribution.setStatus(ContributionStatus.PARTIAL);
            contributionRepository.save(contribution);
        }
        
        // Audit log
        auditService.log(adminId, "VALIDATE_CASH_PAYMENT", "Payment", payment.getId(), 
            String.format("{\"amount\": %s, \"member\": \"%s\", \"notes\": \"%s\"}", 
                request.getAmount(), request.getMemberId(), request.getNotes()));
        
        // Envoyer notification au membre
        try {
            Map<String, String> variables = new HashMap<>();
            variables.put("montant", String.format("%,.0f", payment.getAmount()));
            variables.put("groupe", contribution.getGroup().getNom());
            variables.put("date", LocalDate.now().toString());
            variables.put("reference", payment.getExternalRef());
            
            notificationService.sendNotificationWithTemplate(
                request.getMemberId(),
                NotificationType.PAYMENT_SUCCESS,
                NotificationChannel.PUSH,
                variables
            );
            
            notificationService.sendNotificationWithTemplate(
                request.getMemberId(),
                NotificationType.PAYMENT_SUCCESS,
                NotificationChannel.SMS,
                variables
            );
            
            log.info("✅ Notifications paiement cash envoyées");
        } catch (Exception e) {
            log.error("❌ Erreur notification paiement cash: {}", e.getMessage());
        }
        
        return new PaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByGroup(String groupId) {
        return paymentRepository.findByContribution_Group_Id(groupId)
                .stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPendingPayments() {
        return paymentRepository.findByStatus(PaymentStatus.PENDING)
                .stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
    }
}
