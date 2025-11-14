package com.example.pariba.repositories;

import com.example.pariba.enums.DelegationStatus;
import com.example.pariba.models.Delegation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DelegationRepository extends JpaRepository<Delegation, String> {
    
    List<Delegation> findByGroupId(String groupId);
    
    List<Delegation> findByGrantorId(String grantorId);
    
    List<Delegation> findByProxyId(String proxyId);
    
    List<Delegation> findByGroupIdAndStatus(String groupId, DelegationStatus status);
    
    @Query("SELECT d FROM Delegation d WHERE d.grantor.id = :grantorId AND d.group.id = :groupId AND d.status = 'APPROVED' AND d.validFrom <= :date AND d.validTo >= :date")
    List<Delegation> findActiveDelegationsForGrantorInGroup(
        @Param("grantorId") String grantorId,
        @Param("groupId") String groupId,
        @Param("date") LocalDate date
    );
    
    @Query("SELECT d FROM Delegation d WHERE d.proxy.id = :proxyId AND d.status = 'APPROVED' AND d.validFrom <= :date AND d.validTo >= :date")
    List<Delegation> findActiveDelegationsForProxy(
        @Param("proxyId") String proxyId,
        @Param("date") LocalDate date
    );
    
    @Query("SELECT d FROM Delegation d WHERE d.grantor.id = :grantorId AND d.active = true AND d.validFrom <= :date AND (d.validTo IS NULL OR d.validTo >= :date)")
    List<Delegation> findActiveDelegations(
        @Param("grantorId") String grantorId,
        @Param("date") LocalDate date
    );
    
    @Query("SELECT d FROM Delegation d WHERE d.active = true AND d.validTo < :date")
    List<Delegation> findExpiredDelegations(@Param("date") LocalDate date);
}
