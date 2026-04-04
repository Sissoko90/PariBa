package com.example.pariba.controllers.admin;

import com.example.pariba.dtos.requests.ProcessSubscriptionRequestDTO;
import com.example.pariba.dtos.responses.SubscriptionRequestResponse;
import com.example.pariba.dtos.responses.SubscriptionResponse;
import com.example.pariba.enums.SubscriptionRequestStatus;
import com.example.pariba.exceptions.BadRequestException;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.Person;
import com.example.pariba.models.SubscriptionRequest;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.repositories.SubscriptionRequestRepository;
import com.example.pariba.security.CurrentUser;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.IPushNotificationService;
import com.example.pariba.services.ISubscriptionService;
import com.example.pariba.services.ISystemLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controleur admin pour gerer les demandes d'abonnement
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin - Demandes d'abonnement", description = "Gestion des demandes d'abonnement par l'administrateur")
public class AdminSubscriptionRequestController {

    private final SubscriptionRequestRepository subscriptionRequestRepository;
    private final ISubscriptionService subscriptionService;
    private final CurrentUser currentUser;
    private final PersonRepository personRepository;
    private final IAuditService auditService;
    private final ISystemLogService systemLogService;
    private final IPushNotificationService pushNotificationService;

    // ========================================
    // VUE THYMELEAF
    // ========================================

    /**
     * Page de gestion des demandes d'abonnement
     */
    @GetMapping("/subscription-requests")
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Transactional(readOnly = true)
    public String subscriptionRequestsView(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        
        log.info("Acces a la gestion des demandes d'abonnement");

        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<SubscriptionRequest> requestsPage;

            if (status != null && !status.isEmpty()) {
                SubscriptionRequestStatus requestStatus = SubscriptionRequestStatus.valueOf(status);
                requestsPage = subscriptionRequestRepository.findByStatus(requestStatus, pageRequest);
            } else {
                requestsPage = subscriptionRequestRepository.findAllByOrderByCreatedAtDesc(pageRequest);
            }

            // Statistiques
            Map<String, Long> stats = new HashMap<>();
            stats.put("total", subscriptionRequestRepository.count());
            stats.put("pending", subscriptionRequestRepository.countByStatus(SubscriptionRequestStatus.PENDING));
            stats.put("approved", subscriptionRequestRepository.countByStatus(SubscriptionRequestStatus.APPROVED));
            stats.put("rejected", subscriptionRequestRepository.countByStatus(SubscriptionRequestStatus.REJECTED));

            List<SubscriptionRequestResponse> requests = requestsPage.getContent()
                    .stream()
                    .map(SubscriptionRequestResponse::new)
                    .collect(Collectors.toList());

            model.addAttribute("pageTitle", "Demandes d'Abonnement");
            model.addAttribute("requests", requests);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", requestsPage.getTotalPages());
            model.addAttribute("totalItems", requestsPage.getTotalElements());
            model.addAttribute("stats", stats);
            model.addAttribute("selectedStatus", status);

            return "admin/subscription-requests";
        } catch (Exception e) {
            log.error("Erreur lors du chargement des demandes d'abonnement", e);
            model.addAttribute("error", "Erreur lors du chargement des donnees");
            return "admin/error";
        }
    }

    // ========================================
    // API REST (prefixe /api)
    // ========================================

    /**
     * Obtenir une demande par ID
     */
    @GetMapping("/subscription-requests/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Détails d'une demande", description = "Récupère les détails d'une demande d'abonnement")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Demande récupérée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    public ResponseEntity<SubscriptionRequestResponse> getRequestById(@PathVariable String id) {
        SubscriptionRequest request = subscriptionRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande non trouvee"));
        return ResponseEntity.ok(new SubscriptionRequestResponse(request));
    }

    /**
     * Traiter une demande (approuver ou rejeter)
     */
    @PostMapping("/subscription-requests/{id}/process")
    @ResponseBody
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Transactional
    @Operation(summary = "Traiter une demande", description = "Approuve ou rejette une demande d'abonnement")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Demande traitée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Action invalide ou demande déjà traitée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    public ResponseEntity<SubscriptionRequestResponse> processRequest(
            @PathVariable String id,
            @Valid @RequestBody ProcessSubscriptionRequestDTO dto) {
        
        SubscriptionRequest request = subscriptionRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande non trouvee"));
        
        if (request.getStatus() != SubscriptionRequestStatus.PENDING) {
            throw new BadRequestException("Cette demande a deja ete traitee");
        }
        
        String action = dto.getAction().toUpperCase();
        String adminId = currentUser.getPersonId();
        
        if ("APPROVE".equals(action)) {
            // Approuver et creer l'abonnement
            log.info("Approbation de la demande {} par admin {}", id, adminId);
            
            try {
                // Creer l'abonnement via le service avec période de facturation
                subscriptionService.subscribe(
                    request.getPerson().getId(), 
                    request.getPlan().getId(),
                    request.getBillingPeriod(),
                    request.isAutoRenew()
                );
                
                request.setStatus(SubscriptionRequestStatus.APPROVED);
                request.setAdminNotes(dto.getAdminNotes());
                request.setProcessedAt(Instant.now());
                request.setProcessedBy(adminId);
                
                // Envoyer notification push d'approbation
                sendApprovalNotification(request);
                
            } catch (IllegalStateException e) {
                // Gestion du cas où l'utilisateur a déjà un abonnement actif
                log.error("Erreur lors de l'approbation: {}", e.getMessage());
                
                // Log système ERROR
                String errorDetails = String.format("{\"requestId\":\"%s\",\"personId\":\"%s\",\"planId\":\"%s\",\"error\":\"%s\"}", 
                    id, request.getPerson().getId(), request.getPlan().getId(), e.getMessage());
                systemLogService.log(adminId, "Admin", "SUBSCRIPTION_REQUEST_APPROVAL_FAILED", "SubscriptionRequest", id, errorDetails, "ERROR", false);
                
                throw new BadRequestException("Impossible d'approuver: " + e.getMessage());
            } catch (Exception e) {
                log.error("Erreur inattendue lors de l'approbation: {}", e.getMessage());
                
                // Log système ERROR
                String errorDetails = String.format("{\"requestId\":\"%s\",\"error\":\"%s\"}", id, e.getMessage());
                systemLogService.log(adminId, "Admin", "SUBSCRIPTION_REQUEST_APPROVAL_ERROR", "SubscriptionRequest", id, errorDetails, "ERROR", false);
                
                throw new RuntimeException("Une erreur inattendue s'est produite. Veuillez réessayer.");
            }
            
            // Audit log
            String details = String.format("{\"requestId\":\"%s\",\"personId\":\"%s\",\"personName\":\"%s %s\",\"planId\":\"%s\",\"planName\":\"%s\",\"adminNotes\":\"%s\"}", 
                id, request.getPerson().getId(), request.getPerson().getPrenom(), request.getPerson().getNom(),
                request.getPlan().getId(), request.getPlan().getName(), dto.getAdminNotes() != null ? dto.getAdminNotes() : "");
            auditService.log(adminId, "SUBSCRIPTION_REQUEST_APPROVED", "SubscriptionRequest", id, details);
            
            // System log
            String personName = request.getPerson().getPrenom() + " " + request.getPerson().getNom();
            systemLogService.log(adminId, personName, "SUBSCRIPTION_REQUEST_APPROVED", "SubscriptionRequest", id, details, "INFO", true);
            
            log.info("Demande {} approuvee, abonnement cree", id);
            
        } else if ("REJECT".equals(action)) {
            // Rejeter la demande
            log.info("Rejet de la demande {} par admin {}", id, adminId);
            
            request.setStatus(SubscriptionRequestStatus.REJECTED);
            request.setAdminNotes(dto.getAdminNotes());
            request.setProcessedAt(Instant.now());
            request.setProcessedBy(adminId);
            
            // Envoyer notification push de rejet
            sendRejectionNotification(request);
            
            // Audit log
            String details = String.format("{\"requestId\":\"%s\",\"personId\":\"%s\",\"personName\":\"%s %s\",\"planId\":\"%s\",\"planName\":\"%s\",\"adminNotes\":\"%s\"}", 
                id, request.getPerson().getId(), request.getPerson().getPrenom(), request.getPerson().getNom(),
                request.getPlan().getId(), request.getPlan().getName(), dto.getAdminNotes() != null ? dto.getAdminNotes() : "");
            auditService.log(adminId, "SUBSCRIPTION_REQUEST_REJECTED", "SubscriptionRequest", id, details);
            
            // System log
            String personName = request.getPerson().getPrenom() + " " + request.getPerson().getNom();
            systemLogService.log(adminId, personName, "SUBSCRIPTION_REQUEST_REJECTED", "SubscriptionRequest", id, details, "WARNING", true);
            
            log.info("Demande {} rejetee", id);
            
        } else {
            throw new BadRequestException("Action invalide. Utilisez APPROVE ou REJECT");
        }
        
        request = subscriptionRequestRepository.save(request);
        
        return ResponseEntity.ok(new SubscriptionRequestResponse(request));
    }

    /**
     * Approuver une demande (raccourci)
     */
    @PostMapping("/subscription-requests/{id}/approve")
    @ResponseBody
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Transactional
    public ResponseEntity<SubscriptionRequestResponse> approveRequest(
            @PathVariable String id,
            @RequestParam(required = false) String adminNotes) {
        
        ProcessSubscriptionRequestDTO dto = new ProcessSubscriptionRequestDTO();
        dto.setAction("APPROVE");
        dto.setAdminNotes(adminNotes);
        
        return processRequest(id, dto);
    }

    /**
     * Rejeter une demande (raccourci)
     */
    @PostMapping("/subscription-requests/{id}/reject")
    @ResponseBody
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Transactional
    public ResponseEntity<SubscriptionRequestResponse> rejectRequest(
            @PathVariable String id,
            @RequestParam(required = false) String adminNotes) {
        
        ProcessSubscriptionRequestDTO dto = new ProcessSubscriptionRequestDTO();
        dto.setAction("REJECT");
        dto.setAdminNotes(adminNotes);
        
        return processRequest(id, dto);
    }

    /**
     * Creer un abonnement directement pour un utilisateur (sans demande prealable)
     * Permet a l'admin de souscrire un plan pour un utilisateur
     */
    @PostMapping("/subscriptions/create")
    @ResponseBody
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<SubscriptionResponse> createSubscriptionForUser(
            @RequestParam String personId,
            @RequestParam String planId) {
        
        String adminId = currentUser.getPersonId();
        log.info("Admin {} cree un abonnement pour l'utilisateur {} avec le plan {}", adminId, personId, planId);
        
        SubscriptionResponse subscription = subscriptionService.subscribe(personId, planId);
        
        log.info("Abonnement cree avec succes pour l'utilisateur {}", personId);
        
        return ResponseEntity.ok(subscription);
    }

    /**
     * Rechercher un utilisateur par telephone
     */
    @GetMapping("/users/search")
    @ResponseBody
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Person> searchUserByPhone(@RequestParam String phone) {
        log.info("Recherche utilisateur par telephone: {}", phone);
        
        // Normaliser le numero de telephone
        String normalizedPhone = phone.trim();
        if (!normalizedPhone.startsWith("+")) {
            normalizedPhone = "+223" + normalizedPhone;
        }
        
        final String searchPhone = normalizedPhone;
        
        return personRepository.findByPhone(searchPhone)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouve avec ce numero: " + searchPhone));
    }

    /**
     * Recuperer tous les utilisateurs pour le select (DTO simple sans references circulaires)
     */
    @GetMapping("/users/all")
    @ResponseBody
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> getAllUsers() {
        log.info("Recuperation de tous les utilisateurs pour le formulaire d'abonnement");
        java.util.List<Person> users = personRepository.findAll();
        
        java.util.List<java.util.Map<String, Object>> result = users.stream()
            .map(user -> {
                java.util.Map<String, Object> dto = new java.util.HashMap<>();
                dto.put("id", user.getId());
                dto.put("nom", user.getNom());
                dto.put("prenom", user.getPrenom());
                dto.put("phone", user.getPhone());
                dto.put("email", user.getEmail());
                dto.put("role", user.getRole() != null ? user.getRole().name() : null);
                return dto;
            })
            .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(result);
    }
    
    // ========================================
    // MÉTHODES PRIVÉES - NOTIFICATIONS
    // ========================================
    
    /**
     * Envoyer une notification push d'approbation d'abonnement
     */
    private void sendApprovalNotification(SubscriptionRequest request) {
        try {
            Person person = request.getPerson();
            if (person.getFcmToken() != null && !person.getFcmToken().trim().isEmpty()) {
                String title = "🎉 Abonnement approuvé !";
                String body = String.format("Votre demande d'abonnement au plan %s a été approuvée. Profitez de vos nouveaux avantages !", 
                    request.getPlan().getName());
                
                Map<String, String> data = new HashMap<>();
                data.put("type", "subscription_approved");
                data.put("planId", request.getPlan().getId());
                data.put("planName", request.getPlan().getName());
                data.put("billingPeriod", request.getBillingPeriod());
                
                pushNotificationService.sendToDevice(person.getFcmToken(), title, body, data);
                log.info("📱 Notification d'approbation envoyée à {}", person.getPhone());
            } else {
                log.warn("⚠️ Pas de token FCM pour l'utilisateur {}", person.getPhone());
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de la notification d'approbation: {}", e.getMessage());
        }
    }
    
    /**
     * Envoyer une notification push de rejet d'abonnement
     */
    private void sendRejectionNotification(SubscriptionRequest request) {
        try {
            Person person = request.getPerson();
            if (person.getFcmToken() != null && !person.getFcmToken().trim().isEmpty()) {
                String title = "❌ Demande d'abonnement rejetée";
                String body = String.format("Votre demande d'abonnement au plan %s a été rejetée. Contactez le support pour plus d'informations.", 
                    request.getPlan().getName());
                
                Map<String, String> data = new HashMap<>();
                data.put("type", "subscription_rejected");
                data.put("planId", request.getPlan().getId());
                data.put("planName", request.getPlan().getName());
                data.put("adminNotes", request.getAdminNotes() != null ? request.getAdminNotes() : "");
                
                pushNotificationService.sendToDevice(person.getFcmToken(), title, body, data);
                log.info("📱 Notification de rejet envoyée à {}", person.getPhone());
            } else {
                log.warn("⚠️ Pas de token FCM pour l'utilisateur {}", person.getPhone());
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de la notification de rejet: {}", e.getMessage());
        }
    }
}
