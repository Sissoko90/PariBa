package com.example.pariba.repositories;

import com.example.pariba.models.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, String> {
    
    List<Advertisement> findByPlacement(String placement);
    
    List<Advertisement> findByActiveTrue();
    
    List<Advertisement> findByPlacementAndActiveTrue(String placement);
}
