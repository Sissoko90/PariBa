package com.example.pariba.models;

import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

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
    
    @Column(columnDefinition = "TEXT")
    private String metadataJson;
    
    @Transient
    private Map<String, String> metadata;

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
    
    public Map<String, String> getMetadata() {
        if (metadata == null && metadataJson != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                metadata = mapper.readValue(metadataJson, new TypeReference<Map<String, String>>(){});
            } catch (Exception e) {
                metadata = new HashMap<>();
            }
        }
        return metadata;
    }
    
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
        if (metadata != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                this.metadataJson = mapper.writeValueAsString(metadata);
            } catch (Exception e) {
                this.metadataJson = null;
            }
        } else {
            this.metadataJson = null;
        }
    }
}