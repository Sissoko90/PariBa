package com.example.pariba.services;

import com.example.pariba.dtos.requests.DeclarePaymentRequest;
import com.example.pariba.dtos.requests.PaymentRequest;
import com.example.pariba.dtos.requests.ValidatePaymentRequest;
import com.example.pariba.dtos.responses.PaymentHistoryResponse;
import com.example.pariba.dtos.responses.PaymentResponse;



import java.util.List;

public interface IPaymentService {

    // USER
    PaymentResponse declarePayment(String personId, DeclarePaymentRequest request);
// ADMIN
    PaymentResponse verifyPayment(String paymentId, String adminId);

    // ADMIN
    PaymentResponse validatePayment(String adminId, ValidatePaymentRequest request);

    // Compat legacy / fallback
    PaymentResponse processPayment(String personId, PaymentRequest request);

    // READ
    PaymentResponse getPaymentById(String paymentId);
    List<PaymentResponse> getPaymentsByContribution(String contributionId);
    List<PaymentResponse> getPaymentsByPerson(String personId);
    List<PaymentResponse> getPaymentsByGroup(String groupId);
    List<PaymentResponse> getPendingPayments(String adminId, String groupId); // Nécessite admin du groupe
    List<PaymentResponse> getMyPendingPayments(String personId);

    List<PaymentHistoryResponse> getPaymentHistory(String personId, String groupId);
}
