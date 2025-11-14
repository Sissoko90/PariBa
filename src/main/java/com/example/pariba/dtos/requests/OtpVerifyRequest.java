package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OtpVerifyRequest {
    
    @NotBlank(message = ValidationMessages.REQUIRED_TARGET)
    private String target;
    
    @NotBlank(message = ValidationMessages.REQUIRED_OTP_CODE)
    @Size(min = 6, max = 8, message = ValidationMessages.SIZE_OTP_CODE)
    private String code;

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
