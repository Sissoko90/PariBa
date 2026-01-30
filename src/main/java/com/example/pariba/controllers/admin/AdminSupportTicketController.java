package com.example.pariba.controllers.admin;

import com.example.pariba.dtos.responses.SupportTicketResponse;
import com.example.pariba.enums.TicketStatus;
import com.example.pariba.services.ISupportTicketService;
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
    
    public AdminSupportTicketController(ISupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
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
            redirectAttributes.addFlashAttribute("success", "Statut mis à jour");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/support/tickets/" + id;
    }
    
    @PostMapping("/{id}/delete")
    public String deleteTicket(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            supportTicketService.deleteTicket(id);
            redirectAttributes.addFlashAttribute("success", "Ticket supprimé");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/support/tickets";
    }
}
