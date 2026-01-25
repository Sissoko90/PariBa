package com.example.pariba.services.impl;

import com.example.pariba.dtos.requests.CreateSupportTicketRequest;
import com.example.pariba.dtos.responses.SupportTicketResponse;
import com.example.pariba.enums.TicketStatus;
import com.example.pariba.models.SupportTicket;
import com.example.pariba.repositories.SupportTicketRepository;
import com.example.pariba.services.ISupportTicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupportTicketServiceImpl implements ISupportTicketService {
    
    private final SupportTicketRepository ticketRepository;
    
    public SupportTicketServiceImpl(SupportTicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }
    
    @Override
    public SupportTicketResponse createTicket(CreateSupportTicketRequest request, String personId) {
        SupportTicket ticket = new SupportTicket();
        ticket.setPersonId(personId);
        ticket.setType(request.getType());
        ticket.setSubject(request.getSubject());
        ticket.setMessage(request.getMessage());
        ticket.setStatus(TicketStatus.OPEN);
        
        SupportTicket saved = ticketRepository.save(ticket);
        return mapToResponse(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public SupportTicketResponse getTicketById(String id) {
        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé"));
        return mapToResponse(ticket);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketResponse> getTicketsByPerson(String personId) {
        return ticketRepository.findByPersonIdOrderByCreatedAtDesc(personId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketResponse> getTicketsByStatus(TicketStatus status) {
        return ticketRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketResponse> getAllTickets() {
        return ticketRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public SupportTicketResponse respondToTicket(String ticketId, String response, String adminId) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé"));
        
        ticket.setAdminResponse(response);
        ticket.setAdminId(adminId);
        ticket.setRespondedAt(Instant.now());
        ticket.setStatus(TicketStatus.RESOLVED);
        
        SupportTicket updated = ticketRepository.save(ticket);
        return mapToResponse(updated);
    }
    
    @Override
    public SupportTicketResponse updateTicketStatus(String ticketId, TicketStatus status) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket non trouvé"));
        
        ticket.setStatus(status);
        
        if (status == TicketStatus.CLOSED && ticket.getClosedAt() == null) {
            ticket.setClosedAt(Instant.now());
        }
        
        SupportTicket updated = ticketRepository.save(ticket);
        return mapToResponse(updated);
    }
    
    @Override
    public SupportTicketResponse closeTicket(String ticketId) {
        return updateTicketStatus(ticketId, TicketStatus.CLOSED);
    }
    
    @Override
    public void deleteTicket(String ticketId) {
        ticketRepository.deleteById(ticketId);
    }
    
    private SupportTicketResponse mapToResponse(SupportTicket ticket) {
        SupportTicketResponse response = new SupportTicketResponse();
        response.setId(ticket.getId());
        response.setPersonId(ticket.getPersonId());
        response.setType(ticket.getType());
        response.setStatus(ticket.getStatus());
        response.setPriority(ticket.getPriority());
        response.setSubject(ticket.getSubject());
        response.setMessage(ticket.getMessage());
        response.setAdminResponse(ticket.getAdminResponse());
        response.setAdminId(ticket.getAdminId());
        response.setRespondedAt(ticket.getRespondedAt());
        response.setClosedAt(ticket.getClosedAt());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());
        return response;
    }
}
