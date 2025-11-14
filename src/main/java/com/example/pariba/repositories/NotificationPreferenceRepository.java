package com.example.pariba.repositories;

import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.models.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, String> {
    
    List<NotificationPreference> findByPersonId(String personId);
    
    Optional<NotificationPreference> findByPersonIdAndNotificationTypeAndChannel(
        String personId, 
        NotificationType notificationType, 
        NotificationChannel channel
    );
    
    boolean existsByPersonIdAndNotificationTypeAndChannelAndEnabledTrue(
        String personId,
        NotificationType notificationType,
        NotificationChannel channel
    );
}
