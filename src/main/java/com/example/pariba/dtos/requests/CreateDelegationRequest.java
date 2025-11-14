package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CreateDelegationRequest {
    
    @NotBlank(message = ValidationMessages.REQUIRED_GROUP_ID)
    private String groupId;
    
    @NotBlank(message = ValidationMessages.REQUIRED_PROXY_ID)
    private String proxyPersonId;
    
    @NotNull(message = ValidationMessages.REQUIRED_VALID_FROM)
    private LocalDate validFrom;
    
    @NotNull(message = ValidationMessages.REQUIRED_VALID_TO)
    @Future(message = ValidationMessages.FUTURE_END_DATE)
    private LocalDate validTo;

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getProxyPersonId() { return proxyPersonId; }
    public void setProxyPersonId(String proxyPersonId) { this.proxyPersonId = proxyPersonId; }
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }
}
