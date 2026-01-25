package com.example.pariba.services.impl;

import com.example.pariba.dtos.responses.SupportContactResponse;
import com.example.pariba.models.SupportContact;
import com.example.pariba.repositories.SupportContactRepository;
import com.example.pariba.services.ISupportContactService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SupportContactServiceImpl implements ISupportContactService {
    
    private final SupportContactRepository contactRepository;
    
    public SupportContactServiceImpl(SupportContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public SupportContactResponse getActiveContact() {
        SupportContact contact = contactRepository.findFirstByActiveTrue()
                .orElseThrow(() -> new RuntimeException("Aucun contact de support actif trouvé"));
        return mapToResponse(contact);
    }
    
    @Override
    public SupportContactResponse updateContact(String id, SupportContactResponse request) {
        SupportContact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact non trouvé"));
        
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setWhatsappNumber(request.getWhatsappNumber());
        contact.setSupportHours(request.getSupportHours());
        contact.setActive(request.getActive());
        
        SupportContact updated = contactRepository.save(contact);
        return mapToResponse(updated);
    }
    
    @Override
    public SupportContactResponse createContact(SupportContactResponse request) {
        SupportContact contact = new SupportContact();
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setWhatsappNumber(request.getWhatsappNumber());
        contact.setSupportHours(request.getSupportHours());
        contact.setActive(true);
        
        SupportContact saved = contactRepository.save(contact);
        return mapToResponse(saved);
    }
    
    private SupportContactResponse mapToResponse(SupportContact contact) {
        SupportContactResponse response = new SupportContactResponse();
        response.setId(contact.getId());
        response.setEmail(contact.getEmail());
        response.setPhone(contact.getPhone());
        response.setWhatsappNumber(contact.getWhatsappNumber());
        response.setSupportHours(contact.getSupportHours());
        response.setActive(contact.getActive());
        return response;
    }
}
