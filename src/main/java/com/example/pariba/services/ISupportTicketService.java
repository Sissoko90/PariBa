package com.example.pariba.services;

import com.example.pariba.dtos.requests.CreateSupportTicketRequest;
import com.example.pariba.dtos.responses.SupportTicketResponse;
import com.example.pariba.enums.TicketStatus;

import java.util.List;

public interface ISupportTicketService {
    
    SupportTicketResponse createTicket(CreateSupportTicketRequest request, String personId);
    
    SupportTicketResponse getTicketById(String id);
    
    List<SupportTicketResponse> getTicketsByPerson(String personId);
    
    List<SupportTicketResponse> getTicketsByStatus(TicketStatus status);
    
    List<SupportTicketResponse> getAllTickets();
    
    SupportTicketResponse respondToTicket(String ticketId, String response, String adminId);
    
    SupportTicketResponse updateTicketStatus(String ticketId, TicketStatus status);
    
    SupportTicketResponse closeTicket(String ticketId);
    
    void deleteTicket(String ticketId);
}
