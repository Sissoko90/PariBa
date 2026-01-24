package com.example.pariba.repositories;

import com.example.pariba.models.TontineGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TontineGroupRepository extends JpaRepository<TontineGroup, String> {
    
    List<TontineGroup> findByCreatorId(String creatorId);
    
    @Query("SELECT DISTINCT g FROM TontineGroup g " +
           "JOIN g.memberships m " +
           "WHERE m.person.id = :personId")
    List<TontineGroup> findGroupsByMemberId(@Param("personId") String personId);
    
    @Query("SELECT g FROM TontineGroup g " +
           "WHERE g.creator.id = :personId " +
           "OR EXISTS (SELECT 1 FROM GroupMembership gm WHERE gm.group.id = g.id AND gm.person.id = :personId)")
    List<TontineGroup> findAllGroupsForPerson(@Param("personId") String personId);
    
    // Méthodes pour admin
    Page<TontineGroup> findByNomContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String nom, String description, Pageable pageable);
    
    // Méthodes pour statistiques
    long countByCreatedAtAfter(LocalDateTime date);
    
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Méthodes pour le scheduler de notifications
    List<TontineGroup> findByStartDate(java.time.LocalDate startDate);
}
