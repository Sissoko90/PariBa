package com.example.pariba.controllers;

import com.example.pariba.dtos.requests.SubscriptionRequestDTO;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.SubscriptionRequestResponse;
import com.example.pariba.dtos.responses.SubscriptionResponse;
import com.example.pariba.dtos.responses.SubscriptionPlanResponse;
import com.example.pariba.enums.SubscriptionRequestStatus;
import com.example.pariba.exceptions.BadRequestException;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.enums.SubscriptionStatus;
import com.example.pariba.models.Person;
import com.example.pariba.models.Subscription;
import com.example.pariba.models.SubscriptionPlan;
import com.example.pariba.models.SubscriptionRequest;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.repositories.SubscriptionPlanRepository;
import com.example.pariba.repositories.SubscriptionRepository;
import com.example.pariba.repositories.SubscriptionRequestRepository;
import com.example.pariba.services.ISubscriptionService;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.ISystemLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controleur REST pour la gestion des abonnements (utilisateur)
 * Tous les endpoints necessitent une authentification
 */
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Abonnements", description = "Gestion des abonnements utilisateur")
public class SubscriptionController {
    
    private final ISubscriptionService subscriptionService;
    private final SubscriptionRequestRepository subscriptionRequestRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PersonRepository personRepository;
    private final IAuditService auditService;
    private final ISystemLogService systemLogService;
    
    /**
     * Recupere l'abonnement actif de l'utilisateur connecte
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mon abonnement actif", description = "Récupère l'abonnement actif de l'utilisateur connecté")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Abonnement récupéré"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getMySubscription(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String personId = userDetails.getUsername();
        SubscriptionResponse subscription = subscriptionService.getActiveSubscription(personId);
        
        if (subscription == null) {
            return ResponseEntity.ok(ApiResponse.success("Aucun abonnement actif", null));
        }
        
        return ResponseEntity.ok(ApiResponse.success("Abonnement recupere avec succes", subscription));
    }
    
    /**
     * Liste les plans d'abonnement disponibles (accessible a tous les utilisateurs authentifies)
     */
    @GetMapping("/plans")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Plans disponibles", description = "Liste tous les plans d'abonnement actifs")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des plans")
    })
    public ResponseEntity<ApiResponse<List<SubscriptionPlanResponse>>> getAvailablePlans() {
        List<SubscriptionPlanResponse> plans = subscriptionPlanRepository.findAll()
                .stream()
                .filter(SubscriptionPlan::getActive)
                .map(SubscriptionPlanResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Plans disponibles", plans));
    }
    
    /**
     * Initie une demande d'abonnement (en attente de validation admin)
     * Seuls les utilisateurs authentifies peuvent faire une demande
     */
    @PostMapping("/request")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    @Operation(summary = "Demander un abonnement", description = "Crée une demande d'abonnement en attente de validation admin")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Demande créée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Demande déjà en attente ou plan inactif"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Plan non trouvé")
    })
    public ResponseEntity<ApiResponse<SubscriptionRequestResponse>> requestSubscription(
            @Valid @RequestBody SubscriptionRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String personId = userDetails.getUsername();
        log.info("Demande d'abonnement initiee par {} pour le plan {}", personId, request.getPlanId());
        
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouve"));
        
        SubscriptionPlan plan = subscriptionPlanRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan non trouve"));
        
        if (!plan.getActive()) {
            throw new BadRequestException("Ce plan n'est plus disponible");
        }
        
        // Verifier si une demande en attente existe deja
        if (subscriptionRequestRepository.existsByPersonAndStatus(person, SubscriptionRequestStatus.PENDING)) {
            throw new BadRequestException("Vous avez deja une demande d'abonnement en attente");
        }
        
        // Vérifier si l'utilisateur a déjà un abonnement actif
        List<Subscription> activeSubscriptions = subscriptionRepository.findByPersonIdOrderByCreatedAtDesc(person.getId())
            .stream()
            .filter(s -> s.getStatus() == SubscriptionStatus.ACTIVE)
            .collect(Collectors.toList());
            
        if (!activeSubscriptions.isEmpty()) {
            Subscription activeSub = activeSubscriptions.get(0);
            throw new BadRequestException("Vous avez déjà un abonnement actif au plan '" + activeSub.getPlan().getName() + 
                "'. Vous ne pouvez pas demander un nouvel abonnement tant que votre abonnement actuel n'est pas expiré.");
        }
        
        // Creer la demande
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setPerson(person);
        subscriptionRequest.setPlan(plan);
        subscriptionRequest.setStatus(SubscriptionRequestStatus.PENDING);
        subscriptionRequest.setNotes(request.getNotes());
        subscriptionRequest.setBillingPeriod(request.getBillingPeriod());
        // Auto-renewal automatique pour les abonnements annuels
        subscriptionRequest.setAutoRenew("annual".equalsIgnoreCase(request.getBillingPeriod()));
        
        subscriptionRequest = subscriptionRequestRepository.save(subscriptionRequest);
        log.info("Demande d'abonnement {} creee avec succes", subscriptionRequest.getId());
        
        // Logs
        String details = String.format("{\"requestId\":\"%s\",\"planId\":\"%s\",\"planName\":\"%s\"}", 
            subscriptionRequest.getId(), plan.getId(), plan.getName());
        auditService.log(personId, "SUBSCRIPTION_REQUESTED", "SubscriptionRequest", subscriptionRequest.getId(), details);
        systemLogService.log(personId, person.getPrenom() + " " + person.getNom(), "SUBSCRIPTION_REQUESTED", "SubscriptionRequest", subscriptionRequest.getId(), details, "INFO", true);
        
        // Utiliser le constructeur avec Person et Plan deja charges pour eviter lazy loading
        return ResponseEntity.ok(ApiResponse.success(
                "Demande d'abonnement envoyee. Elle sera traitee par un administrateur.",
                new SubscriptionRequestResponse(subscriptionRequest, person, plan)));
    }
    
    /**
     * Liste mes demandes d'abonnement
     */
    @GetMapping("/requests")
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    @Operation(summary = "Mes demandes d'abonnement", description = "Liste toutes mes demandes d'abonnement")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des demandes")
    })
    public ResponseEntity<ApiResponse<List<SubscriptionRequestResponse>>> getMyRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String personId = userDetails.getUsername();
        
        List<SubscriptionRequestResponse> requests = subscriptionRequestRepository
                .findByPersonIdOrderByCreatedAtDesc(personId)
                .stream()
                .map(SubscriptionRequestResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Mes demandes d'abonnement", requests));
    }
    
    /**
     * Annule une demande d'abonnement en attente
     */
    @PostMapping("/requests/{requestId}/cancel")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Annuler une demande", description = "Annule une demande d'abonnement en attente")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Demande annulée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Demande non annulable"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    public ResponseEntity<ApiResponse<Void>> cancelRequest(
            @PathVariable String requestId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String personId = userDetails.getUsername();
        
        SubscriptionRequest request = subscriptionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande non trouvee"));
        
        if (!request.getPerson().getId().equals(personId)) {
            throw new BadRequestException("Vous ne pouvez pas annuler cette demande");
        }
        
        if (request.getStatus() != SubscriptionRequestStatus.PENDING) {
            throw new BadRequestException("Seules les demandes en attente peuvent etre annulees");
        }
        
        request.setStatus(SubscriptionRequestStatus.CANCELLED);
        subscriptionRequestRepository.save(request);
        
        // Logs
        String details = String.format("{\"requestId\":\"%s\"}", requestId);
        auditService.log(personId, "SUBSCRIPTION_REQUEST_CANCELLED", "SubscriptionRequest", requestId, details);
        systemLogService.log(personId, "User", "SUBSCRIPTION_REQUEST_CANCELLED", "SubscriptionRequest", requestId, details, "INFO", true);
        
        return ResponseEntity.ok(ApiResponse.success("Demande annulee avec succes", null));
    }
    
    /**
     * Verifie l'acces a une fonctionnalite premium
     */
    @GetMapping("/feature/{feature}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Vérifier accès fonctionnalité", description = "Vérifie si l'utilisateur a accès à une fonctionnalité premium")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Résultat de la vérification")
    })
    public ResponseEntity<ApiResponse<Boolean>> checkFeatureAccess(
            @PathVariable String feature,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String personId = userDetails.getUsername();
        boolean hasAccess = subscriptionService.hasFeatureAccess(personId, feature);
        
        return ResponseEntity.ok(ApiResponse.success(
                hasAccess ? "Acces autorise" : "Acces refuse", hasAccess));
    }
}
