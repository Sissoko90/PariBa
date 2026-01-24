package com.example.pariba.repositories;

import com.example.pariba.enums.TourStatus;
import com.example.pariba.models.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<Tour, String> {
    
    List<Tour> findByGroupIdOrderByIndexInGroupAsc(String groupId);
    
    List<Tour> findByGroupIdAndStatus(String groupId, TourStatus status);
    
    Optional<Tour> findByGroupIdAndIndexInGroup(String groupId, Integer indexInGroup);
    
    List<Tour> findByBeneficiaryId(String beneficiaryId);
    
    @Query("SELECT t FROM Tour t WHERE t.group.id = :groupId AND t.status = :status ORDER BY t.indexInGroup ASC")
    List<Tour> findByGroupIdAndStatusOrderByIndex(@Param("groupId") String groupId, @Param("status") TourStatus status);
    
    @Query("SELECT t FROM Tour t WHERE t.group.id = :groupId AND t.status = 'IN_PROGRESS'")
    Optional<Tour> findCurrentTourByGroupId(@Param("groupId") String groupId);
    
    // Méthodes pour le scheduler de notifications
    List<Tour> findByScheduledDateAndStatus(java.time.LocalDate scheduledDate, TourStatus status);
}
