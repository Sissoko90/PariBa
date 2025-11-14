package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {
    
    @NotBlank(message = ValidationMessages.REQUIRED_TARGET)
    private String target;
    
    @NotBlank(message = ValidationMessages.REQUIRED_OTP_CODE)
    private String otpCode;
    
    @NotBlank(message = ValidationMessages.REQUIRED_PASSWORD)
    @Size(min = 8, message = ValidationMessages.SIZE_PASSWORD)
    private String newPassword;

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
