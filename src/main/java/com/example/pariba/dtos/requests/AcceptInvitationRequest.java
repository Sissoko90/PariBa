package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;

public class AcceptInvitationRequest {
    
    @NotBlank(message = ValidationMessages.REQUIRED_INVITATION_CODE)
    private String linkCode;

    public String getLinkCode() { return linkCode; }
    public void setLinkCode(String linkCode) { this.linkCode = linkCode; }
}
