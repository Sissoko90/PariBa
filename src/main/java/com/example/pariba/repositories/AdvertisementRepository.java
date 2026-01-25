package com.example.pariba.repositories;

import com.example.pariba.enums.AdPlacement;
import com.example.pariba.models.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, String> {
    
    List<Advertisement> findByPlacement(AdPlacement placement);
    
    List<Advertisement> findByActiveTrue();
    
    List<Advertisement> findByPlacementAndActiveTrue(AdPlacement placement);
    
    /**
     * Incrémente atomiquement le compteur d'impressions
     */
    @Modifying
    @Query("UPDATE Advertisement a SET a.impressions = a.impressions + 1 WHERE a.id = :adId")
    void incrementImpressions(@Param("adId") String adId);
    
    /**
     * Incrémente atomiquement le compteur de clics
     */
    @Modifying
    @Query("UPDATE Advertisement a SET a.clicks = a.clicks + 1 WHERE a.id = :adId")
    void incrementClicks(@Param("adId") String adId);
}
