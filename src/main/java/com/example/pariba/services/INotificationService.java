package com.example.pariba.services;

import com.example.pariba.dtos.responses.NotificationResponse;
import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;

import java.util.List;
import java.util.Map;

public interface INotificationService {
    void sendNotification(String personId, NotificationType type, String title, String message, NotificationChannel channel);
    void sendNotificationWithTemplate(String personId, NotificationType type, NotificationChannel channel, Map<String, String> variables);
    void sendBulkNotification(List<String> personIds, NotificationType type, String title, String message, NotificationChannel channel);
    List<NotificationResponse> getNotificationsByPerson(String personId);
    List<NotificationResponse> getUnreadNotifications(String personId);
    void markAsRead(String notificationId, String personId);
    void markAllAsRead(String personId);
    void saveFcmToken(String personId, String fcmToken);
    void deleteNotification(String notificationId, String personId);
    void deleteAllNotifications(String personId);
}
    
