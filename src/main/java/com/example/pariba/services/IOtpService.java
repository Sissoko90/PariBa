package com.example.pariba.services;

import com.example.pariba.enums.NotificationChannel;

public interface IOtpService {
    String generateAndSendOtp(String target);
    String generateAndSendOtp(String target, NotificationChannel channel);
    boolean verifyOtp(String target, String code);
    void cleanupExpiredOtps();
}
