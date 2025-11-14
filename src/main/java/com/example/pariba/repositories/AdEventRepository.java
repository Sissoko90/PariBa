package com.example.pariba.repositories;

import com.example.pariba.models.AdEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdEventRepository extends JpaRepository<AdEvent, String> {
    
    List<AdEvent> findByAdId(String adId);
    
    List<AdEvent> findByPersonId(String personId);
    
    List<AdEvent> findByAdIdAndType(String adId, String type);
    
    @Query("SELECT COUNT(e) FROM AdEvent e WHERE e.ad.id = :adId AND e.type = 'impression'")
    long countImpressionsByAdId(@Param("adId") String adId);
    
    @Query("SELECT COUNT(e) FROM AdEvent e WHERE e.ad.id = :adId AND e.type = 'click'")
    long countClicksByAdId(@Param("adId") String adId);
}
