package com.example.pariba.services;

import com.example.pariba.dtos.requests.CashPaymentRequest;
import com.example.pariba.dtos.requests.PaymentRequest;
import com.example.pariba.dtos.responses.PaymentResponse;

import java.util.List;

public interface IPaymentService {
    PaymentResponse processPayment(String personId, PaymentRequest request);
    PaymentResponse validateCashPayment(String adminId, CashPaymentRequest request);
    PaymentResponse getPaymentById(String paymentId);
    List<PaymentResponse> getPaymentsByContribution(String contributionId);
    List<PaymentResponse> getPaymentsByPerson(String personId);
    List<PaymentResponse> getPaymentsByGroup(String groupId);
    List<PaymentResponse> getPendingPayments();
    void verifyPayment(String paymentId, String personId);
}
