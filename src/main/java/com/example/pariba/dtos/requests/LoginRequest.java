package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    
    @NotBlank(message = ValidationMessages.REQUIRED_USERNAME)
    private String username; // email ou phone
    
    @NotBlank(message = ValidationMessages.REQUIRED_PASSWORD)
    private String password;
    
    @NotBlank(message = "Le code OTP est requis")
    private String otpCode;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
}
