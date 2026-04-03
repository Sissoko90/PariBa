package com.example.pariba.controllers.admin;

import com.example.pariba.dtos.responses.SupportTicketResponse;
import com.example.pariba.enums.TicketStatus;
import com.example.pariba.services.ISupportTicketService;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.ISystemLogService;
import com.example.pariba.security.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/support/tickets")
@PreAuthorize("hasRole('SUPERADMIN')")
public class AdminSupportTicketController {
    
    private final ISupportTicketService supportTicketService;
    private final IAuditService auditService;
    private final ISystemLogService systemLogService;
    private final CurrentUser currentUser;
    
    public AdminSupportTicketController(ISupportTicketService supportTicketService, IAuditService auditService, ISystemLogService systemLogService, CurrentUser currentUser) {
        this.supportTicketService = supportTicketService;
        this.auditService = auditService;
        this.systemLogService = systemLogService;
        this.currentUser = currentUser;
    }
    
    @GetMapping
    public String listTickets(@RequestParam(required = false) String status, Model model) {
        List<SupportTicketResponse> tickets;
        
        if (status != null && !status.isEmpty()) {
            tickets = supportTicketService.getTicketsByStatus(TicketStatus.valueOf(status));
        } else {
            tickets = supportTicketService.getAllTickets();
        }
        
        model.addAttribute("tickets", tickets);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", TicketStatus.values());
        
        return "admin/support/tickets-list";
    }
    
    @GetMapping("/{id}")
    public String viewTicket(@PathVariable String id, Model model) {
        SupportTicketResponse ticket = supportTicketService.getTicketById(id);
        model.addAttribute("ticket", ticket);
        model.addAttribute("statuses", TicketStatus.values());
        return "admin/support/ticket-details";
    }
    
    @PostMapping("/{id}/respond")
    public String respondToTicket(
            @PathVariable String id,
            @RequestParam String response,
            @RequestParam(required = false) String status,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {
        try {
            // Récupérer l'ID de l'admin connecté
            String adminId = authentication.getName(); // ou récupérer depuis le principal
            supportTicketService.respondToTicket(id, response, adminId);
            
            // Mettre à jour le statut si fourni
            if (status != null && !status.isEmpty()) {
                supportTicketService.updateTicketStatus(id, TicketStatus.valueOf(status));
            }
            
            // Logs
            String details = String.format("{\"ticketId\":\"%s\",\"status\":\"%s\"}", id, status);
            auditService.log(currentUser.getPersonId(), "TICKET_RESPONDED", "SupportTicket", id, details);
            systemLogService.log(currentUser.getPersonId(), "Admin", "TICKET_RESPONDED", "SupportTicket", id, details, "INFO", true);
            
            redirectAttributes.addFlashAttribute("success", "Réponse envoyée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/support/tickets/" + id;
    }
    
    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable String id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        try {
            supportTicketService.updateTicketStatus(id, TicketStatus.valueOf(status));
            
            String details = String.format("{\"ticketId\":\"%s\",\"newStatus\":\"%s\"}", id, status);
            auditService.log(currentUser.getPersonId(), "TICKET_STATUS_UPDATED", "SupportTicket", id, details);
            systemLogService.log(currentUser.getPersonId(), "Admin", "TICKET_STATUS_UPDATED", "SupportTicket", id, details, "INFO", true);
            
            redirectAttributes.addFlashAttribute("success", "Statut mis à jour");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/support/tickets/" + id;
    }
    
    @PostMapping("/{id}/delete")
    public String deleteTicket(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            String details = String.format("{\"ticketId\":\"%s\"}", id);
            auditService.log(currentUser.getPersonId(), "TICKET_DELETED", "SupportTicket", id, details);
            systemLogService.log(currentUser.getPersonId(), "Admin", "TICKET_DELETED", "SupportTicket", id, details, "WARNING", true);
            
            supportTicketService.deleteTicket(id);
            redirectAttributes.addFlashAttribute("success", "Ticket supprimé");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/support/tickets";
    }
}
