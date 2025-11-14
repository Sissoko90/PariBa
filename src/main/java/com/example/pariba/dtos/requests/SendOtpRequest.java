package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;

public class SendOtpRequest {
    
    @NotBlank(message = ValidationMessages.REQUIRED_TARGET)
    private String target; // email ou phone
    
    private String channel; // SMS, EMAIL, WHATSAPP

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}
