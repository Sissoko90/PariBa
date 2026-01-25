package com.example.pariba.controllers.admin;

import com.example.pariba.dtos.requests.CashPaymentRequest;
import com.example.pariba.dtos.responses.PaymentResponse;
import com.example.pariba.services.IPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Contrôleur admin pour la gestion des paiements
 */
@RestController
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPaymentController {
    
    private final IPaymentService paymentService;
    
    /**
     * Valider un paiement cash pour un membre
     */
    @PostMapping("/cash/validate")
    public ResponseEntity<PaymentResponse> validateCashPayment(
            @Valid @RequestBody CashPaymentRequest request,
            Principal principal) {
        
        PaymentResponse payment = paymentService.validateCashPayment(
            principal.getName(), 
            request
        );
        
        return ResponseEntity.ok(payment);
    }
    
    /**
     * Obtenir tous les paiements d'un groupe
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<PaymentResponse>> getGroupPayments(
            @PathVariable String groupId) {
        
        List<PaymentResponse> payments = paymentService.getPaymentsByGroup(groupId);
        return ResponseEntity.ok(payments);
    }
    
    /**
     * Obtenir les paiements en attente de validation
     */
    @GetMapping("/pending")
    public ResponseEntity<List<PaymentResponse>> getPendingPayments() {
        List<PaymentResponse> payments = paymentService.getPendingPayments();
        return ResponseEntity.ok(payments);
    }
}
