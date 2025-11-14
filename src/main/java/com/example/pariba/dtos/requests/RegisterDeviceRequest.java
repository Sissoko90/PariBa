package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterDeviceRequest {
    
    @NotBlank(message = ValidationMessages.REQUIRED_TOKEN)
    private String token; // FCM/APNs token
    
    @NotBlank(message = ValidationMessages.REQUIRED_PLATFORM)
    @Pattern(regexp = "^(ios|android)$", message = ValidationMessages.INVALID_PLATFORM)
    private String platform;
    
    @Size(max = 100, message = "Le nom de l'appareil ne peut pas dépasser 100 caractères")
    private String deviceName; // iPhone 15, Samsung Galaxy S24, etc.
    
    @Size(max = 50, message = "La version de l'app ne peut pas dépasser 50 caractères")
    private String appVersion; // 1.0.0
    
    @Size(max = 50, message = "La version de l'OS ne peut pas dépasser 50 caractères")
    private String osVersion; // iOS 17.1, Android 14

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }
    public String getOsVersion() { return osVersion; }
    public void setOsVersion(String osVersion) { this.osVersion = osVersion; }
}
