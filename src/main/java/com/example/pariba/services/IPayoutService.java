package com.example.pariba.services;

import com.example.pariba.dtos.requests.PayoutRequest;
import com.example.pariba.dtos.responses.PayoutResponse;

import java.util.List;

public interface IPayoutService {
    PayoutResponse processPayout(String personId, PayoutRequest request);
    PayoutResponse getPayoutById(String payoutId);
    List<PayoutResponse> getPayoutsByTour(String tourId);
    List<PayoutResponse> getPayoutsByBeneficiary(String personId);
}
