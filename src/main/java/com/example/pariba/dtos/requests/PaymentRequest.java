package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import com.example.pariba.enums.PaymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PaymentRequest {
    
    @NotBlank(message = ValidationMessages.REQUIRED_CONTRIBUTION_ID)
    private String contributionId;
    
    @NotNull(message = ValidationMessages.REQUIRED_AMOUNT)
    @DecimalMin(value = "1.0", message = ValidationMessages.MIN_AMOUNT_PAYMENT)
    private BigDecimal amount;
    
    @NotNull(message = ValidationMessages.REQUIRED_PAYMENT_TYPE)
    private PaymentType paymentType;
    
    private String externalRef; // Référence de l'opérateur mobile money

    public String getContributionId() { return contributionId; }
    public void setContributionId(String contributionId) { this.contributionId = contributionId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
    public String getExternalRef() { return externalRef; }
    public void setExternalRef(String externalRef) { this.externalRef = externalRef; }
}
