package com.example.pariba.repositories;

import com.example.pariba.models.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, String> {
    
    Page<SystemLog> findByOrderByCreatedAtDesc(Pageable pageable);
    
    Page<SystemLog> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);
    
    Page<SystemLog> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    
    Page<SystemLog> findByLevelOrderByCreatedAtDesc(String level, Pageable pageable);
    
    List<SystemLog> findTop100ByOrderByCreatedAtDesc();
    
    @Query("SELECT COUNT(s) FROM SystemLog s WHERE s.createdAt >= :since")
    long countSince(LocalDateTime since);
    
    @Query("SELECT COUNT(s) FROM SystemLog s WHERE s.level = 'ERROR' AND s.createdAt >= :since")
    long countErrorsSince(LocalDateTime since);
    
    @Query("SELECT s FROM SystemLog s WHERE " +
           "LOWER(s.action) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.userName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.details) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY s.createdAt DESC")
    Page<SystemLog> searchLogs(String search, Pageable pageable);
    
    @Query("SELECT s FROM SystemLog s WHERE s.level = :level AND (" +
           "LOWER(s.action) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.userName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.details) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY s.createdAt DESC")
    Page<SystemLog> searchLogsByLevel(String level, String search, Pageable pageable);
}
