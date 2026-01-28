package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.DeclarePaymentRequest;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.PaymentResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.pariba.services.IPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.example.pariba.dtos.requests.ValidatePaymentRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@Tag(name = "Paiements", description = "Gestion des paiements (Orange Money, Moov Money, etc.)")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final IPaymentService paymentService;

    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

     @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Déclarer un paiement", 
               description = "Déclare un paiement pour une contribution (Mobile Money ou Cash). " +
                           "Le paiement sera en statut PENDING jusqu'à validation par l'admin.")
    public ResponseEntity<ApiResponse<PaymentResponse>> declarePayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DeclarePaymentRequest request) {
        PaymentResponse payment = paymentService.declarePayment(userDetails.getUsername(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.PAYMENT_SUCCESS_PROCESSED, payment));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Détails d'un paiement")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable String id) {
        PaymentResponse payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payment));
    }

    @GetMapping("/contribution/{contributionId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Paiements d'une contribution")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByContribution(@PathVariable String contributionId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByContribution(contributionId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payments));
    }

    @GetMapping("/person/{personId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Paiements d'une personne")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByPerson(@PathVariable String personId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByPerson(personId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payments));
    }

    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Paiements d'un groupe")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByGroup(@PathVariable String groupId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByGroup(groupId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payments));
    }

    @GetMapping("/group/{groupId}/pending")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Paiements en attente d'un groupe", 
               description = "⚠️ Nécessite d'être ADMIN du groupe. Permet de voir tous les membres qui n'ont pas encore payé.")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPendingPayments(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String groupId) {
        // Vérification admin du groupe effectuée dans le service
        List<PaymentResponse> payments = paymentService.getPendingPayments(userDetails.getUsername(), groupId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payments));
    }

    @GetMapping("/me/pending")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Mes paiements en attente")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMyPendingPayments(@AuthenticationPrincipal UserDetails userDetails) {
        List<PaymentResponse> payments = paymentService.getMyPendingPayments(userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payments));
    }

    // ===================== ADMIN DU GROUPE =====================
    // Note: @PreAuthorize vérifie que l'utilisateur est authentifié (USER)
    // La vérification du rôle ADMIN du groupe est effectuée dans le service (PaymentServiceImpl)
    // via membershipRepository.findByGroupIdAndPersonId() qui vérifie GroupRole.ADMIN

    @PostMapping("/{id}/verify")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Vérifier un paiement", 
               description = "⚠️ Nécessite d'être ADMIN du groupe (vérifié dans le service)")
    public ResponseEntity<ApiResponse<Void>> verifyPayment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String id) {
        // La vérification du rôle admin du groupe est faite dans paymentService.verifyPayment()
        paymentService.verifyPayment(id, userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, null));
    }

    @PostMapping("/validate")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Valider un paiement (confirmé ou rejeté)", 
               description = "⚠️ Nécessite d'être ADMIN du groupe (vérifié dans le service)")
    public ResponseEntity<ApiResponse<PaymentResponse>> validatePayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ValidatePaymentRequest request) {
        // La vérification du rôle admin du groupe est faite dans paymentService.validatePayment()
        // Ligne 217-223: vérifie membership.getRole().name().equals("ADMIN")
        PaymentResponse payment = paymentService.validatePayment(userDetails.getUsername(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payment));
    }

    // ===================== CALLBACK =====================

    @PostMapping("/orange/callback")
    @Operation(summary = "Callback Orange Money")
    public ResponseEntity<String> orangeMoneyCallback(@RequestBody String payload) {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/moov/callback")
    @Operation(summary = "Callback Moov Money")
    public ResponseEntity<String> moovMoneyCallback(@RequestBody String payload) {
        return ResponseEntity.ok("OK");
    }
}