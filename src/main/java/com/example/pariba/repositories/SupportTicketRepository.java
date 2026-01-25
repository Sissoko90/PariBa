package com.example.pariba.repositories;

import com.example.pariba.enums.TicketStatus;
import com.example.pariba.models.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, String> {
    
    List<SupportTicket> findByPersonIdOrderByCreatedAtDesc(String personId);
    
    List<SupportTicket> findByStatusOrderByCreatedAtDesc(TicketStatus status);
    
    List<SupportTicket> findByPersonIdAndStatusOrderByCreatedAtDesc(String personId, TicketStatus status);
    
    List<SupportTicket> findAllByOrderByCreatedAtDesc();
    
    long countByStatus(TicketStatus status);
    
    long countByPersonId(String personId);
}
