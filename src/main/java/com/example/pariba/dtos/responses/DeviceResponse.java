package com.example.pariba.dtos.responses;

import com.example.pariba.models.DeviceToken;
import java.time.Instant;
import java.time.LocalDateTime;

public class DeviceResponse {
    
    private String id;
    private String token;
    private String platform;
    private String deviceName;
    private String appVersion;
    private String osVersion;
    private boolean active;
    private LocalDateTime lastUsedAt;
    private Instant createdAt;

    public DeviceResponse() {}

    public DeviceResponse(DeviceToken deviceToken) {
        this.id = deviceToken.getId();
        this.token = deviceToken.getToken();
        this.platform = deviceToken.getPlatform();
        this.deviceName = deviceToken.getDeviceName();
        this.appVersion = deviceToken.getAppVersion();
        this.osVersion = deviceToken.getOsVersion();
        this.active = deviceToken.isActive();
        this.lastUsedAt = deviceToken.getLastUsedAt();
        this.createdAt = deviceToken.getCreatedAt();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
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
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public LocalDateTime getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(LocalDateTime lastUsedAt) { this.lastUsedAt = lastUsedAt; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
