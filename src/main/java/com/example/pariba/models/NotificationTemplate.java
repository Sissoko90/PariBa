package com.example.pariba.models;

import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Modèle pour les templates de notifications configurables
 */
@Entity
@Table(name = "notification_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode(callSuper = false)
public class NotificationTemplate extends BaseEntity {
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;
    
    @Column(nullable = true)
    private String subject; // Pour email (optionnel pour PUSH/SMS)
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String bodyTemplate; // Template avec variables {{nom}}, {{montant}}, etc.
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column(nullable = false)
    private String language = "fr"; // fr, en, etc.
    
    private Instant createdAt;
    private Instant updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
