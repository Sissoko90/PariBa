package com.example.pariba.repositories;

import com.example.pariba.enums.ExportStatus;
import com.example.pariba.models.ExportJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExportJobRepository extends JpaRepository<ExportJob, String> {
    
    List<ExportJob> findByGroupId(String groupId);
    
    List<ExportJob> findByGroupIdAndStatus(String groupId, ExportStatus status);
    
    List<ExportJob> findByStatus(ExportStatus status);
    
    List<ExportJob> findByGroupIdOrderByCreatedAtDesc(String groupId);
    
    List<ExportJob> findByRequestedByIdOrderByCreatedAtDesc(String requestedById);
    
    @Query("SELECT e FROM ExportJob e WHERE e.status = 'COMPLETED' AND e.createdAt < :cutoffDate")
    List<ExportJob> findOldCompletedJobs(@Param("cutoffDate") LocalDateTime cutoffDate);
}
