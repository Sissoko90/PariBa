package com.example.pariba.repositories;

import com.example.pariba.models.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {
    
    List<AuditLog> findByActorIdOrderByCreatedAtDesc(String actorId);
    
    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, String entityId);
    
    List<AuditLog> findByActionOrderByCreatedAtDesc(String action);
    
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :start AND :end ORDER BY a.createdAt DESC")
    List<AuditLog> findByDateRange(@Param("start") Instant start, @Param("end") Instant end);
    
    @Query("SELECT a FROM AuditLog a WHERE a.entityType = :entityType AND a.entityId = :entityId AND a.createdAt BETWEEN :start AND :end ORDER BY a.createdAt DESC")
    List<AuditLog> findByEntityAndDateRange(
        @Param("entityType") String entityType,
        @Param("entityId") String entityId,
        @Param("start") Instant start,
        @Param("end") Instant end
    );
}
