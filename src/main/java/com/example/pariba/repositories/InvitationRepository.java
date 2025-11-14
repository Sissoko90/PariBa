package com.example.pariba.repositories;

import com.example.pariba.enums.InvitationStatus;
import com.example.pariba.models.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, String> {
    
    List<Invitation> findByGroupId(String groupId);
    
    List<Invitation> findByGroupIdAndStatus(String groupId, InvitationStatus status);
    
    Optional<Invitation> findByLinkCode(String linkCode);
    
    List<Invitation> findByTargetPhone(String targetPhone);
    
    List<Invitation> findByTargetEmail(String targetEmail);
    
    void deleteByExpiresAtBeforeAndStatus(Instant now, InvitationStatus status);
}
