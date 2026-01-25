package com.example.pariba.repositories;

import com.example.pariba.enums.GuideCategory;
import com.example.pariba.models.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuideRepository extends JpaRepository<Guide, String> {
    
    List<Guide> findByActiveOrderByDisplayOrderAscCreatedAtDesc(Boolean active);
    
    List<Guide> findByCategoryAndActiveOrderByDisplayOrderAscCreatedAtDesc(GuideCategory category, Boolean active);
    
    List<Guide> findAllByOrderByDisplayOrderAscCreatedAtDesc();
    
    long countByActive(Boolean active);
    
    long countByCategory(GuideCategory category);
}
