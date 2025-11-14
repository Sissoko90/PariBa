package com.example.pariba.models;

import jakarta.persistence.*;

@Entity
@Table(name = "device_tokens", indexes = { @Index(columnList = "person_id"), @Index(columnList = "token", unique = true) })
public class DeviceToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(nullable = false, unique = true, length = 512)
    private String token; // FCM/APNs

    @Column(nullable = false, length = 20)
    private String platform; // ios/android
    
    @Column(length = 100)
    private String deviceName; // iPhone 15, Samsung Galaxy S24, etc.
    
    @Column(length = 50)
    private String appVersion; // 1.0.0
    
    @Column(length = 50)
    private String osVersion; // iOS 17.1, Android 14
    
    private boolean active = true;
    private java.time.LocalDateTime lastUsedAt;

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean getActive() { return active; }
    
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    
    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }
    
    public String getOsVersion() { return osVersion; }
    public void setOsVersion(String osVersion) { this.osVersion = osVersion; }
    
    public java.time.LocalDateTime getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(java.time.LocalDateTime lastUsedAt) { this.lastUsedAt = lastUsedAt; }
}