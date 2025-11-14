package com.example.pariba.dtos.responses;

import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.models.Notification;

import java.time.Instant;

public class NotificationResponse {
    
    private String id;
    private NotificationType type;
    private NotificationChannel channel;
    private String title;
    private String body;
    private Instant scheduledAt;
    private Instant sentAt;
    private boolean readFlag;
    private Instant createdAt;

    public NotificationResponse() {}

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.type = notification.getType();
        this.channel = notification.getChannel();
        this.title = notification.getTitle();
        this.body = notification.getBody();
        this.scheduledAt = notification.getScheduledAt();
        this.sentAt = notification.getSentAt();
        this.readFlag = notification.isReadFlag();
        this.createdAt = notification.getCreatedAt();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public NotificationChannel getChannel() { return channel; }
    public void setChannel(NotificationChannel channel) { this.channel = channel; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Instant getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(Instant scheduledAt) { this.scheduledAt = scheduledAt; }
    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
    public boolean isReadFlag() { return readFlag; }
    public void setReadFlag(boolean readFlag) { this.readFlag = readFlag; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
