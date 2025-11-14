package com.example.pariba.repositories;

import com.example.pariba.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    Optional<User> findByPersonId(String personId);
    
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.person.email = :identifier OR u.person.phone = :identifier")
    Optional<User> findByUsernameOrEmailOrPhone(@Param("identifier") String identifier);
}
