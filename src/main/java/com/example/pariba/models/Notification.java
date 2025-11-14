package com.example.pariba.models;

import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notifications", indexes = { @Index(columnList = "person_id"), @Index(columnList = "type") })
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel = NotificationChannel.PUSH;

    private String title;
    @Column(length = 2048) private String body;

    private Instant scheduledAt;
    private Instant sentAt;
    private boolean readFlag = false;

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }
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
}