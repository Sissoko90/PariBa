package com.example.pariba.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "otp_tokens", indexes = { @Index(columnList = "target") })
public class OtpToken extends BaseEntity {

    @Column(nullable = false)
    private String target; // phone or email

    @Column(nullable = false, length = 8)
    private String code;

    @Column(nullable = false)
    private Instant expiresAt;

    private boolean used = false;

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}