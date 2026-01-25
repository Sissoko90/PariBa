package com.example.pariba.services.impl;

import com.example.pariba.dtos.responses.FAQResponse;
import com.example.pariba.enums.FAQCategory;
import com.example.pariba.models.FAQ;
import com.example.pariba.repositories.FAQRepository;
import com.example.pariba.services.IFAQService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FAQServiceImpl implements IFAQService {
    
    private final FAQRepository faqRepository;
    
    public FAQServiceImpl(FAQRepository faqRepository) {
        this.faqRepository = faqRepository;
    }
    
    @Override
    public FAQResponse createFAQ(FAQResponse request) {
        FAQ faq = new FAQ();
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        faq.setCategory(request.getCategory());
        faq.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        faq.setActive(request.getActive() != null ? request.getActive() : true);
        
        FAQ saved = faqRepository.save(faq);
        return mapToResponse(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public FAQResponse getFAQById(String id) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ non trouvée"));
        return mapToResponse(faq);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FAQResponse> getAllFAQs() {
        return faqRepository.findAllByOrderByDisplayOrderAscCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FAQResponse> getActiveFAQs() {
        return faqRepository.findByActiveOrderByDisplayOrderAscCreatedAtDesc(true)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FAQResponse> getFAQsByCategory(FAQCategory category) {
        return faqRepository.findByCategoryAndActiveOrderByDisplayOrderAscCreatedAtDesc(category, true)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public FAQResponse updateFAQ(String id, FAQResponse request) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ non trouvée"));
        
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        faq.setCategory(request.getCategory());
        faq.setDisplayOrder(request.getDisplayOrder());
        faq.setActive(request.getActive());
        
        FAQ updated = faqRepository.save(faq);
        return mapToResponse(updated);
    }
    
    @Override
    public void incrementViewCount(String id) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ non trouvée"));
        faq.setViewCount(faq.getViewCount() + 1);
        faqRepository.save(faq);
    }
    
    @Override
    public void deleteFAQ(String id) {
        faqRepository.deleteById(id);
    }
    
    private FAQResponse mapToResponse(FAQ faq) {
        FAQResponse response = new FAQResponse();
        response.setId(faq.getId());
        response.setQuestion(faq.getQuestion());
        response.setAnswer(faq.getAnswer());
        response.setCategory(faq.getCategory());
        response.setDisplayOrder(faq.getDisplayOrder());
        response.setActive(faq.getActive());
        response.setViewCount(faq.getViewCount());
        response.setCreatedAt(faq.getCreatedAt());
        response.setUpdatedAt(faq.getUpdatedAt());
        return response;
    }
}
