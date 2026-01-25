package com.example.pariba.repositories;

import com.example.pariba.enums.FAQCategory;
import com.example.pariba.models.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, String> {
    
    List<FAQ> findByActiveOrderByDisplayOrderAscCreatedAtDesc(Boolean active);
    
    List<FAQ> findByCategoryAndActiveOrderByDisplayOrderAscCreatedAtDesc(FAQCategory category, Boolean active);
    
    List<FAQ> findAllByOrderByDisplayOrderAscCreatedAtDesc();
    
    long countByActive(Boolean active);
    
    long countByCategory(FAQCategory category);
}
