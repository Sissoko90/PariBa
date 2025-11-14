package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
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
    @Operation(summary = "Effectuer un paiement", description = "Traite un paiement pour une contribution")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paiement traité"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PaymentRequest request) {
        String personId = userDetails.getUsername();
        PaymentResponse payment = paymentService.processPayment(personId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.PAYMENT_SUCCESS_PROCESSED, payment));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Détails d'un paiement", description = "Récupère les détails d'un paiement")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paiement trouvé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    })
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable String id) {
        PaymentResponse payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payment));
    }

    @GetMapping("/contribution/{contributionId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Paiements d'une contribution", description = "Récupère tous les paiements d'une contribution")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des paiements")
    })
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByContribution(@PathVariable String contributionId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByContribution(contributionId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payments));
    }

    @GetMapping("/person/{personId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Paiements d'une personne", description = "Récupère tous les paiements d'une personne")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des paiements")
    })
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByPerson(@PathVariable String personId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByPerson(personId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payments));
    }

    @PostMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Vérifier un paiement", description = "Vérifie et valide un paiement. Nécessite d'être ADMIN ou TREASURER du groupe.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paiement vérifié"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<ApiResponse<Void>> verifyPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        String personId = userDetails.getUsername();
        paymentService.verifyPayment(id, personId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, null));
    }

    @PostMapping("/orange/callback")
    @Operation(summary = "Callback Orange Money", description = "Endpoint de callback pour les notifications Orange Money")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Callback traité")
    })
    public ResponseEntity<String> orangeMoneyCallback(@RequestBody String payload) {
        // TODO: Implémenter le callback Orange Money
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/moov/callback")
    @Operation(summary = "Callback Moov Money", description = "Endpoint de callback pour les notifications Moov Money")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Callback traité")
    })
    public ResponseEntity<String> moovMoneyCallback(@RequestBody String payload) {
        // TODO: Implémenter le callback Moov Money
        return ResponseEntity.ok("OK");
    }
}
