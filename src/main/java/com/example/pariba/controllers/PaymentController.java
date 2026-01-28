package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.DeclarePaymentRequest;
import com.example.pariba.dtos.requests.PaymentRequest;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.PaymentResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.pariba.services.IPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.example.pariba.dtos.requests.ValidatePaymentRequest;
import com.example.pariba.dtos.requests.CashPaymentRequest;

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
    @Operation(summary = "Déclarer un paiement", description = "Déclare un paiement pour une contribution")
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
    @Operation(summary = "Paiements en attente d'un groupe")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPendingPayments(@PathVariable String groupId) {
        List<PaymentResponse> payments = paymentService.getPendingPayments(groupId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payments));
    }

    @GetMapping("/me/pending")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Mes paiements en attente")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMyPendingPayments(@AuthenticationPrincipal UserDetails userDetails) {
        List<PaymentResponse> payments = paymentService.getMyPendingPayments(userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payments));
    }

    // ===================== ADMIN =====================

    @PostMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Vérifier un paiement")
    public ResponseEntity<ApiResponse<Void>> verifyPayment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String id) {
        paymentService.verifyPayment(id, userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, null));
    }

    @PostMapping("/validate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Valider un paiement (confirmé ou rejeté)")
    public ResponseEntity<ApiResponse<PaymentResponse>> validatePayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ValidatePaymentRequest request) {
        PaymentResponse payment = paymentService.validatePayment(userDetails.getUsername(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payment));
    }

    @PostMapping("/validate-cash")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Valider un paiement cash")
    public ResponseEntity<ApiResponse<PaymentResponse>> validateCashPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CashPaymentRequest request) {
        PaymentResponse payment = paymentService.validateCashPayment(userDetails.getUsername(), request);
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