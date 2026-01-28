package com.example.pariba.services.impl;

import com.example.pariba.constants.AppConstants;
import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.DeclarePaymentRequest;
import com.example.pariba.dtos.requests.PaymentRequest;
import com.example.pariba.dtos.requests.CashPaymentRequest;
import com.example.pariba.dtos.requests.ValidatePaymentRequest;
import com.example.pariba.dtos.responses.PaymentResponse;
import com.example.pariba.enums.ContributionStatus;
import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.enums.PaymentStatus;
import com.example.pariba.enums.PaymentType;
import com.example.pariba.exceptions.BadRequestException;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.Contribution;
import com.example.pariba.models.GroupMembership;
import com.example.pariba.models.Payment;
import com.example.pariba.repositories.ContributionRepository;
import com.example.pariba.repositories.GroupMembershipRepository;
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
import java.time.LocalDateTime;
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
    private final GroupMembershipRepository membershipRepository;
    private final IContributionService contributionService;
    private final IAuditService auditService;
    private final INotificationService notificationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                             ContributionRepository contributionRepository,
                             PersonRepository personRepository,
                             GroupMembershipRepository membershipRepository,
                             IContributionService contributionService,
                             IAuditService auditService,
                             INotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.contributionRepository = contributionRepository;
        this.personRepository = personRepository;
        this.membershipRepository = membershipRepository;
        this.contributionService = contributionService;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public PaymentResponse declarePayment(String personId, DeclarePaymentRequest request) {
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

        // Vérifier que le membre n'a pas déjà un paiement en attente pour cette contribution
        List<Payment> pendingPayments = paymentRepository.findByContributionId(contribution.getId())
                .stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING || p.getStatus() == PaymentStatus.PROCESSING)
                .collect(Collectors.toList());
        
        if (!pendingPayments.isEmpty()) {
            throw new BadRequestException("Vous avez déjà un paiement en attente pour cette contribution");
        }

        // Créer le paiement déclaré (pas encore confirmé)
        Payment payment = new Payment();
        payment.setContribution(contribution);
        payment.setGroup(contribution.getGroup());
        payment.setPayer(personRepository.findById(personId).orElseThrow());
        payment.setAmount(request.getAmount());
        payment.setPaymentType(request.getPaymentType());
        payment.setStatus(PaymentStatus.PENDING); // IMPORTANT: En attente de validation
        payment.setExternalRef(request.getTransactionRef());
        payment.setNotes(request.getNotes());
        payment.setPayout(false);

        payment = paymentRepository.save(payment);

        // NE PAS marquer la contribution comme payée - attendre validation admin
        // contributionService.markAsPaid(contribution.getId()); // À RETIRER

        // Audit log
        auditService.log(personId, AppConstants.AUDIT_DECLARE_PAYMENT, "Payment", payment.getId(),
            String.format("{\"amount\": %s, \"type\": \"%s\", \"ref\": \"%s\"}", 
                request.getAmount(), request.getPaymentType(), request.getTransactionRef()));

        // Envoyer notification de déclaration de paiement à l'admin
        try {
            Map<String, String> variables = new HashMap<>();
            variables.put("membre", contribution.getMember().getPrenom() + " " + contribution.getMember().getNom());
            variables.put("montant", String.format("%,.0f", payment.getAmount()));
            variables.put("groupe", contribution.getGroup().getNom());
            variables.put("methode", payment.getPaymentType().getLabel());
            variables.put("reference", payment.getExternalRef() != null ? payment.getExternalRef() : "N/A");
            variables.put("notes", payment.getNotes() != null ? payment.getNotes() : "");
            variables.put("date", LocalDateTime.now().toString());
            
            // Notifier l'admin du groupe
            notificationService.sendNotificationWithTemplate(
                contribution.getGroup().getCreator().getId(),
                NotificationType.PAYMENT_DECLARED,
                NotificationChannel.PUSH,
                variables
            );
            
            // Envoyer aussi par Email à l'admin
            notificationService.sendNotificationWithTemplate(
                contribution.getGroup().getCreator().getId(),
                NotificationType.PAYMENT_DECLARED,
                NotificationChannel.EMAIL,
                variables
            );
            
            // Confirmer à l'utilisateur que sa déclaration est enregistrée
            Map<String, String> userVars = new HashMap<>();
            userVars.put("montant", String.format("%,.0f", payment.getAmount()));
            userVars.put("groupe", contribution.getGroup().getNom());
            userVars.put("methode", payment.getPaymentType().getLabel());
            userVars.put("reference", payment.getExternalRef() != null ? payment.getExternalRef() : "N/A");
            
            notificationService.sendNotificationWithTemplate(
                personId,
                NotificationType.PAYMENT_DECLARATION_RECEIVED,
                NotificationChannel.PUSH,
                userVars
            );
            
            log.info("✅ Notifications déclaration paiement envoyées");
        } catch (Exception e) {
            log.error("❌ Erreur notification déclaration paiement: {}", e.getMessage());
        }

        return new PaymentResponse(payment);
    }
    @Override
    @Transactional
    public PaymentResponse processPayment(String personId, PaymentRequest request) {
        // TODO: implémenter la logique de paiement réel
        throw new UnsupportedOperationException("Méthode processPayment non implémentée");
    }

    @Override
    @Transactional
    public PaymentResponse verifyPayment(String paymentId, String adminId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        // Vérifier que l'admin est bien admin du groupe
        boolean isAdmin = membershipRepository.findByGroupIdAndPersonId(payment.getGroup().getId(), adminId)
                .map(m -> m.getRole().name().equals("ADMIN"))
                .orElse(false);

        if (!isAdmin) {
            throw new BadRequestException("Seul l'administrateur du groupe peut vérifier les paiements");
        }

        // Ici tu peux décider ce que "vérifier" signifie
        // Par exemple, marquer le paiement comme PROCESSING ou CONFIRMED
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setValidatedBy(personRepository.findById(adminId).orElseThrow());
        payment.setValidatedAt(LocalDateTime.now());

        payment = paymentRepository.save(payment);

        // Audit
        auditService.log(adminId, "VERIFY_PAYMENT", "Payment", payment.getId(),
                String.format("{\"status\": \"%s\"}", payment.getStatus()));

        return new PaymentResponse(payment);
    }



    @Override
    @Transactional
    public PaymentResponse validatePayment(String adminId, ValidatePaymentRequest request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", request.getPaymentId()));

        // Vérifier que la personne est admin du groupe
        boolean isAdmin = membershipRepository.findByGroupIdAndPersonId(payment.getGroup().getId(), adminId)
                .map(membership -> membership.getRole().name().equals("ADMIN"))
                .orElse(false);
        
        if (!isAdmin) {
            throw new BadRequestException("Seul l'administrateur du groupe peut valider les paiements");
        }

        // Mettre à jour le statut du paiement
        payment.setStatus(request.isConfirmed() ? PaymentStatus.CONFIRMED : PaymentStatus.REJECTED);
        payment.setAdminNotes(request.getNotes());
        payment.setValidatedBy(personRepository.findById(adminId).orElseThrow());
        payment.setValidatedAt(LocalDateTime.now());
        
        payment = paymentRepository.save(payment);

        // Si le paiement est confirmé, vérifier si la contribution est maintenant payée
        if (payment.getStatus() == PaymentStatus.CONFIRMED) {
            Contribution contribution = payment.getContribution();
            
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
        }

        // Audit log
        auditService.log(adminId, "VALIDATE_PAYMENT", "Payment", payment.getId(),
            String.format("{\"status\": \"%s\", \"notes\": \"%s\"}", 
                payment.getStatus(), request.getNotes()));

        // Envoyer notification au membre
        try {
            Map<String, String> variables = new HashMap<>();
            variables.put("montant", String.format("%,.0f", payment.getAmount()));
            variables.put("groupe", payment.getGroup().getNom());
            variables.put("date", LocalDate.now().toString());
            variables.put("reference", payment.getExternalRef() != null ? payment.getExternalRef() : "N/A");
            variables.put("status", payment.getStatus().getLabel());
            variables.put("admin_notes", request.getNotes() != null ? request.getNotes() : "");
            
            if (payment.getStatus() == PaymentStatus.CONFIRMED) {
                notificationService.sendNotificationWithTemplate(
                    payment.getPayer().getId(),
                    NotificationType.PAYMENT_VALIDATED,
                    NotificationChannel.PUSH,
                    variables
                );
                
                notificationService.sendNotificationWithTemplate(
                    payment.getPayer().getId(),
                    NotificationType.PAYMENT_VALIDATED,
                    NotificationChannel.SMS,
                    variables
                );
            } else if (payment.getStatus() == PaymentStatus.REJECTED) {
                variables.put("raison", request.getNotes() != null ? request.getNotes() : "Non spécifiée");
                
                notificationService.sendNotificationWithTemplate(
                    payment.getPayer().getId(),
                    NotificationType.PAYMENT_REJECTED,
                    NotificationChannel.PUSH,
                    variables
                );
            }
            
            log.info("✅ Notifications validation paiement envoyées");
        } catch (Exception e) {
            log.error("❌ Erreur notification validation paiement: {}", e.getMessage());
        }

        return new PaymentResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse validateCashPayment(String adminId, CashPaymentRequest request) {
        // Vérifier que l'admin est bien admin du groupe
        Contribution contribution = contributionRepository.findById(request.getContributionId())
                .orElseThrow(() -> new ResourceNotFoundException("Contribution", "id", request.getContributionId()));
        
        // Vérifier que la personne est admin du groupe
        boolean isAdmin = membershipRepository.findByGroupIdAndPersonId(contribution.getGroup().getId(), adminId)
                .map(membership -> membership.getRole().name().equals("ADMIN"))
                .orElse(false);
        
        if (!isAdmin) {
            throw new BadRequestException("Seul l'administrateur du groupe peut valider les paiements cash");
        }
        
        // Créer le paiement cash (déjà confirmé par l'admin)
        Payment payment = new Payment();
        payment.setContribution(contribution);
        payment.setGroup(contribution.getGroup());
        payment.setPayer(contribution.getMember());
        payment.setAmount(request.getAmount());
        payment.setPaymentType(PaymentType.CASH);
        payment.setStatus(PaymentStatus.CONFIRMED); // Directement confirmé par l'admin
        payment.setExternalRef("CASH-" + System.currentTimeMillis());
        payment.setNotes(request.getNotes());
        payment.setPayout(false);
        payment.setValidatedBy(personRepository.findById(adminId).orElseThrow());
        payment.setValidatedAt(LocalDateTime.now());
        
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
                request.getAmount(), contribution.getMember().getId(), request.getNotes()));
        
        // Envoyer notification au membre
        try {
            Map<String, String> variables = new HashMap<>();
            variables.put("montant", String.format("%,.0f", payment.getAmount()));
            variables.put("groupe", contribution.getGroup().getNom());
            variables.put("date", LocalDate.now().toString());
            variables.put("reference", payment.getExternalRef());
            
            notificationService.sendNotificationWithTemplate(
                contribution.getMember().getId(),
                NotificationType.PAYMENT_VALIDATED,
                NotificationChannel.PUSH,
                variables
            );
            
            notificationService.sendNotificationWithTemplate(
                contribution.getMember().getId(),
                NotificationType.PAYMENT_VALIDATED,
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
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByGroup(String groupId) {
        return paymentRepository.findByGroupIdOrderByCreatedAtDesc(groupId)
                .stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPendingPayments(String groupId) {
        return paymentRepository.findByGroupIdAndStatus(groupId, PaymentStatus.PENDING)
                .stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getMyPendingPayments(String personId) {
        return paymentRepository.findByPayerId(personId)
                .stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING)
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
    }
}