package com.example.pariba.repositories;

import com.example.pariba.enums.ContributionStatus;
import com.example.pariba.models.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, String> {
    
    List<Contribution> findByGroupId(String groupId);
    
    List<Contribution> findByTourId(String tourId);
    
    List<Contribution> findByMemberId(String memberId);
    
    List<Contribution> findByGroupIdAndMemberId(String groupId, String memberId);
    
    List<Contribution> findByGroupIdAndStatus(String groupId, ContributionStatus status);
    
    List<Contribution> findByTourIdAndStatus(String tourId, ContributionStatus status);
    
    @Query("SELECT c FROM Contribution c WHERE c.status = 'DUE' AND c.dueDate < :date")
    List<Contribution> findOverdueContributions(@Param("date") LocalDate date);
    
    @Query("SELECT c FROM Contribution c WHERE c.member.id = :memberId AND c.status = 'DUE' ORDER BY c.dueDate ASC")
    List<Contribution> findPendingContributionsByMember(@Param("memberId") String memberId);
    
    @Query("SELECT c FROM Contribution c WHERE c.group.id = :groupId AND c.status = 'DUE' ORDER BY c.dueDate ASC")
    List<Contribution> findPendingContributionsByGroup(@Param("groupId") String groupId);
    
    @Query("SELECT c FROM Contribution c WHERE c.status = 'DUE' AND c.dueDate < :date ORDER BY c.dueDate ASC")
    List<Contribution> findLateContributions(@Param("date") LocalDate date);
    
    long countByTourIdAndStatus(String tourId, ContributionStatus status);
    
    // Méthodes pour le scheduler de notifications
    List<Contribution> findByDueDateAndStatus(LocalDate dueDate, ContributionStatus status);
    
    List<Contribution> findByDueDateBeforeAndStatus(LocalDate dueDate, ContributionStatus status);
}
