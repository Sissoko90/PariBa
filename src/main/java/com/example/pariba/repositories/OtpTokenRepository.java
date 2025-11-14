package com.example.pariba.repositories;

import com.example.pariba.models.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, String> {
    
    Optional<OtpToken> findByTargetAndCodeAndUsedFalseAndExpiresAtAfter(
        String target, 
        String code, 
        Instant now
    );
    
    void deleteByExpiresAtBefore(Instant now);
}
