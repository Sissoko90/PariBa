package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.PayoutRequest;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.PayoutResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.pariba.services.IPayoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payouts")
public class PayoutController {

    private final IPayoutService payoutService;

    public PayoutController(IPayoutService payoutService) {
        this.payoutService = payoutService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PayoutResponse>> processPayout(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PayoutRequest request) {
        String personId = userDetails.getUsername();
        PayoutResponse payout = payoutService.processPayout(personId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.PAYMENT_SUCCESS_PAYOUT, payout));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PayoutResponse>> getPayoutById(@PathVariable String id) {
        PayoutResponse payout = payoutService.getPayoutById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payout));
    }

    @GetMapping("/tour/{tourId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<PayoutResponse>>> getPayoutsByTour(@PathVariable String tourId) {
        List<PayoutResponse> payouts = payoutService.getPayoutsByTour(tourId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payouts));
    }

    @GetMapping("/beneficiary/{personId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<PayoutResponse>>> getPayoutsByBeneficiary(@PathVariable String personId) {
        List<PayoutResponse> payouts = payoutService.getPayoutsByBeneficiary(personId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, payouts));
    }
}
