package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import com.example.pariba.enums.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PayoutRequest {
    
    @NotBlank(message = ValidationMessages.REQUIRED_TOUR_ID)
    private String tourId;
    
    @NotNull(message = ValidationMessages.REQUIRED_PAYMENT_TYPE)
    private PaymentType paymentType;
    
    private String externalRef; // Référence de transaction

    public String getTourId() { return tourId; }
    public void setTourId(String tourId) { this.tourId = tourId; }
    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
    public String getExternalRef() { return externalRef; }
    public void setExternalRef(String externalRef) { this.externalRef = externalRef; }
}
