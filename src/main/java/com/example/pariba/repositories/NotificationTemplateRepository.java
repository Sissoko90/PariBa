package com.example.pariba.repositories;

import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.models.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, String> {
    
    Optional<NotificationTemplate> findByTypeAndChannelAndLanguageAndActiveTrue(
        NotificationType type, 
        NotificationChannel channel, 
        String language
    );
    
    Optional<NotificationTemplate> findByTypeAndChannelAndActiveTrue(
        NotificationType type,
        NotificationChannel channel
    );
    
    List<NotificationTemplate> findByActiveTrue();
    
    List<NotificationTemplate> findByType(NotificationType type);
    
    List<NotificationTemplate> findByChannel(NotificationChannel channel);
}
