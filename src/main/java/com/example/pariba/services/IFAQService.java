package com.example.pariba.services;

import com.example.pariba.dtos.responses.FAQResponse;
import com.example.pariba.enums.FAQCategory;

import java.util.List;

public interface IFAQService {
    
    FAQResponse createFAQ(FAQResponse request);
    
    FAQResponse getFAQById(String id);
    
    List<FAQResponse> getAllFAQs();
    
    List<FAQResponse> getActiveFAQs();
    
    List<FAQResponse> getFAQsByCategory(FAQCategory category);
    
    FAQResponse updateFAQ(String id, FAQResponse request);
    
    void incrementViewCount(String id);
    
    void deleteFAQ(String id);
}
