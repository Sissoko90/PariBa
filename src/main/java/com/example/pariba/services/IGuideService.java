package com.example.pariba.services;

import com.example.pariba.dtos.responses.GuideResponse;
import com.example.pariba.enums.GuideCategory;

import java.util.List;

public interface IGuideService {
    
    GuideResponse createGuide(GuideResponse request);
    
    GuideResponse getGuideById(String id);
    
    List<GuideResponse> getAllGuides();
    
    List<GuideResponse> getActiveGuides();
    
    List<GuideResponse> getGuidesByCategory(GuideCategory category);
    
    GuideResponse updateGuide(String id, GuideResponse request);
    
    void incrementViewCount(String id);
    
    void deleteGuide(String id);
}
