package com.example.pariba.repositories;

import com.example.pariba.models.Payout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, String> {
    
    List<Payout> findByTourId(String tourId);
    
    boolean existsByTourId(String tourId);
    
    List<Payout> findByBeneficiaryId(String beneficiaryId);
}
