package com.example.pariba.controllers.admin;

import com.example.pariba.dtos.responses.FAQResponse;
import com.example.pariba.enums.FAQCategory;
import com.example.pariba.services.IFAQService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/support/faqs")
@PreAuthorize("hasRole('SUPERADMIN')")
public class AdminFAQController {
    
    private final IFAQService faqService;
    
    public AdminFAQController(IFAQService faqService) {
        this.faqService = faqService;
    }
    
    @GetMapping
    public String listFAQs(Model model) {
        List<FAQResponse> faqs = faqService.getAllFAQs();
        model.addAttribute("faqs", faqs);
        model.addAttribute("categories", FAQCategory.values());
        return "admin/support/faqs-list";
    }
    
    @GetMapping("/new")
    public String newFAQForm(Model model) {
        FAQResponse faq = new FAQResponse();
        faq.setActive(true);
        faq.setDisplayOrder(0);
        model.addAttribute("faq", faq);
        model.addAttribute("categories", FAQCategory.values());
        model.addAttribute("isEdit", false);
        return "admin/support/faq-form";
    }
    
    @GetMapping("/{id}/edit")
    public String editFAQForm(@PathVariable String id, Model model) {
        FAQResponse faq = faqService.getFAQById(id);
        model.addAttribute("faq", faq);
        model.addAttribute("categories", FAQCategory.values());
        model.addAttribute("isEdit", true);
        return "admin/support/faq-form";
    }
    
    @PostMapping("/new")
    public String createFAQ(@ModelAttribute FAQResponse faq, RedirectAttributes redirectAttributes) {
        try {
            faqService.createFAQ(faq);
            redirectAttributes.addFlashAttribute("success", "FAQ créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/support/faqs";
    }
    
    @PostMapping("/{id}/edit")
    public String updateFAQ(@PathVariable String id, @ModelAttribute FAQResponse faq, RedirectAttributes redirectAttributes) {
        try {
            faqService.updateFAQ(id, faq);
            redirectAttributes.addFlashAttribute("success", "FAQ mise à jour avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/support/faqs";
    }
    
    @PostMapping("/{id}/delete")
    public String deleteFAQ(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            faqService.deleteFAQ(id);
            redirectAttributes.addFlashAttribute("success", "FAQ supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
        }
        return "redirect:/admin/support/faqs";
    }
}
