package com.example.pariba.repositories;

import com.example.pariba.enums.NotificationType;
import com.example.pariba.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    
    List<Notification> findByPersonId(String personId);
    
    List<Notification> findByPersonIdAndReadFlag(String personId, boolean readFlag);
    
    List<Notification> findByPersonIdOrderByCreatedAtDesc(String personId);
    
    List<Notification> findByPersonIdAndReadFlagOrderByCreatedAtDesc(String personId, boolean readFlag);
    
    @Query("SELECT n FROM Notification n WHERE n.scheduledAt <= :now AND n.sentAt IS NULL")
    List<Notification> findPendingNotifications(@Param("now") Instant now);
    
    List<Notification> findByPersonIdAndType(String personId, NotificationType type);
    
    long countByPersonIdAndReadFlag(String personId, boolean readFlag);
    
    long countByReadFlagFalse();
}
