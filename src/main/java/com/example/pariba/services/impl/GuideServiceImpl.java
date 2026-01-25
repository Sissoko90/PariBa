package com.example.pariba.services.impl;

import com.example.pariba.dtos.responses.GuideResponse;
import com.example.pariba.enums.GuideCategory;
import com.example.pariba.models.Guide;
import com.example.pariba.repositories.GuideRepository;
import com.example.pariba.services.IGuideService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GuideServiceImpl implements IGuideService {
    
    private final GuideRepository guideRepository;
    
    public GuideServiceImpl(GuideRepository guideRepository) {
        this.guideRepository = guideRepository;
    }
    
    @Override
    public GuideResponse createGuide(GuideResponse request) {
        Guide guide = new Guide();
        guide.setTitle(request.getTitle());
        guide.setDescription(request.getDescription());
        guide.setContent(request.getContent());
        guide.setCategory(request.getCategory());
        guide.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        guide.setActive(request.getActive() != null ? request.getActive() : true);
        guide.setIconName(request.getIconName());
        guide.setEstimatedReadTime(request.getEstimatedReadTime());
        
        Guide saved = guideRepository.save(guide);
        return mapToResponse(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public GuideResponse getGuideById(String id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guide non trouvé"));
        return mapToResponse(guide);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GuideResponse> getAllGuides() {
        return guideRepository.findAllByOrderByDisplayOrderAscCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GuideResponse> getActiveGuides() {
        return guideRepository.findByActiveOrderByDisplayOrderAscCreatedAtDesc(true)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GuideResponse> getGuidesByCategory(GuideCategory category) {
        return guideRepository.findByCategoryAndActiveOrderByDisplayOrderAscCreatedAtDesc(category, true)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public GuideResponse updateGuide(String id, GuideResponse request) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guide non trouvé"));
        
        guide.setTitle(request.getTitle());
        guide.setDescription(request.getDescription());
        guide.setContent(request.getContent());
        guide.setCategory(request.getCategory());
        guide.setDisplayOrder(request.getDisplayOrder());
        guide.setActive(request.getActive());
        guide.setIconName(request.getIconName());
        guide.setEstimatedReadTime(request.getEstimatedReadTime());
        
        Guide updated = guideRepository.save(guide);
        return mapToResponse(updated);
    }
    
    @Override
    public void incrementViewCount(String id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guide non trouvé"));
        guide.setViewCount(guide.getViewCount() + 1);
        guideRepository.save(guide);
    }
    
    @Override
    public void deleteGuide(String id) {
        guideRepository.deleteById(id);
    }
    
    private GuideResponse mapToResponse(Guide guide) {
        GuideResponse response = new GuideResponse();
        response.setId(guide.getId());
        response.setTitle(guide.getTitle());
        response.setDescription(guide.getDescription());
        response.setContent(guide.getContent());
        response.setCategory(guide.getCategory());
        response.setDisplayOrder(guide.getDisplayOrder());
        response.setActive(guide.getActive());
        response.setViewCount(guide.getViewCount());
        response.setIconName(guide.getIconName());
        response.setEstimatedReadTime(guide.getEstimatedReadTime());
        response.setCreatedAt(guide.getCreatedAt());
        response.setUpdatedAt(guide.getUpdatedAt());
        return response;
    }
}
