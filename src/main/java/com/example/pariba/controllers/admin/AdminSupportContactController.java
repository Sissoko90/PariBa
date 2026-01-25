package com.example.pariba.controllers.admin;

import com.example.pariba.dtos.responses.SupportContactResponse;
import com.example.pariba.services.ISupportContactService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/support/contact")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
public class AdminSupportContactController {
    
    private final ISupportContactService contactService;
    
    public AdminSupportContactController(ISupportContactService contactService) {
        this.contactService = contactService;
    }
    
    @GetMapping
    public String editContact(Model model) {
        try {
            SupportContactResponse contact = contactService.getActiveContact();
            model.addAttribute("contact", contact);
        } catch (Exception e) {
            SupportContactResponse contact = new SupportContactResponse();
            contact.setEmail("makenzyks6@gmail.com");
            contact.setPhone("+223 97 75 86 97");
            contact.setWhatsappNumber("+223 97 75 86 97");
            contact.setSupportHours("Lun-Ven: 8h-18h");
            contact.setActive(true);
            model.addAttribute("contact", contact);
            model.addAttribute("isNew", true);
        }
        return "admin/support/contact-form";
    }
    
    @PostMapping
    public String saveContact(@ModelAttribute SupportContactResponse contact, 
                             @RequestParam(required = false) String contactId,
                             RedirectAttributes redirectAttributes) {
        try {
            if (contactId != null && !contactId.isEmpty()) {
                contactService.updateContact(contactId, contact);
                redirectAttributes.addFlashAttribute("success", "Contact mis à jour avec succès");
            } else {
                contactService.createContact(contact);
                redirectAttributes.addFlashAttribute("success", "Contact créé avec succès");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/support/contact";
    }
}
