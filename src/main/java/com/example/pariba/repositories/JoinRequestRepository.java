package com.example.pariba.repositories;

import com.example.pariba.enums.JoinRequestStatus;
import com.example.pariba.models.JoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JoinRequestRepository extends JpaRepository<JoinRequest, String> {

    @Query("SELECT jr FROM JoinRequest jr WHERE jr.group.id = :groupId AND jr.person.id = :personId")
    Optional<JoinRequest> findByGroupIdAndPersonId(@Param("groupId") String groupId, @Param("personId") String personId);

    @Query("SELECT jr FROM JoinRequest jr WHERE jr.group.id = :groupId AND jr.status = :status ORDER BY jr.createdAt DESC")
    List<JoinRequest> findByGroupIdAndStatus(@Param("groupId") String groupId, @Param("status") JoinRequestStatus status);

    @Query("SELECT jr FROM JoinRequest jr WHERE jr.group.id = :groupId ORDER BY jr.createdAt DESC")
    List<JoinRequest> findByGroupId(@Param("groupId") String groupId);

    @Query("SELECT jr FROM JoinRequest jr WHERE jr.person.id = :personId ORDER BY jr.createdAt DESC")
    List<JoinRequest> findByPersonId(@Param("personId") String personId);

    @Query("SELECT COUNT(jr) FROM JoinRequest jr WHERE jr.group.id = :groupId AND jr.status = 'PENDING'")
    long countPendingByGroupId(@Param("groupId") String groupId);
}
