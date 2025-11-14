package com.example.pariba.repositories;

import com.example.pariba.enums.GroupRole;
import com.example.pariba.models.GroupMembership;
import com.example.pariba.models.GroupMembershipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, GroupMembershipId> {
    
    List<GroupMembership> findByGroupId(String groupId);
    
    List<GroupMembership> findByPersonId(String personId);
    
    Optional<GroupMembership> findByGroupIdAndPersonId(String groupId, String personId);
    
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.group.id = :groupId AND gm.role = :role")
    List<GroupMembership> findByGroupIdAndRole(@Param("groupId") String groupId, @Param("role") GroupRole role);
    
    long countByGroupId(String groupId);
    
    boolean existsByGroupIdAndPersonId(String groupId, String personId);
    
    List<GroupMembership> findByGroupIdAndPersonIdNot(String groupId, String personId);
}
