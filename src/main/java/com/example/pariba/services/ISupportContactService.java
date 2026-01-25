package com.example.pariba.services;

import com.example.pariba.dtos.responses.SupportContactResponse;

public interface ISupportContactService {
    
    SupportContactResponse getActiveContact();
    
    SupportContactResponse updateContact(String id, SupportContactResponse request);
    
    SupportContactResponse createContact(SupportContactResponse request);
}
