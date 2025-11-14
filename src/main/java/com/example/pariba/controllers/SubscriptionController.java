package com.example.pariba.controllers;

import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.SubscriptionResponse;
import com.example.pariba.services.ISubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour la gestion des abonnements
 */
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {
    
    private final ISubscriptionService subscriptionService;
    
    /**
     * Récupère l'abonnement actif de l'utilisateur connecté
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getMySubscription(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String personId = userDetails.getUsername();
        SubscriptionResponse subscription = subscriptionService.getActiveSubscription(personId);
        
        if (subscription == null) {
            return ResponseEntity.ok(ApiResponse.success("Aucun abonnement actif", null));
        }
        
        return ResponseEntity.ok(ApiResponse.success("Abonnement récupéré avec succès", subscription));
    }
    
    /**
     * Crée ou met à niveau un abonnement
     */
    @PostMapping("/subscribe/{planId}")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> subscribe(
            @PathVariable String planId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String personId = userDetails.getUsername();
        SubscriptionResponse subscription = subscriptionService.subscribe(personId, planId);
        
        return ResponseEntity.ok(ApiResponse.success("Abonnement créé/mis à niveau avec succès", subscription));
    }
    
    /**
     * Annule l'abonnement de l'utilisateur connecté
     */
    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelSubscription(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String personId = userDetails.getUsername();
        subscriptionService.cancelSubscription(personId);
        
        return ResponseEntity.ok(ApiResponse.success("Abonnement annulé avec succès", null));
    }
    
    /**
     * Vérifie l'accès à une fonctionnalité premium
     */
    @GetMapping("/feature/{feature}")
    public ResponseEntity<ApiResponse<Boolean>> checkFeatureAccess(
            @PathVariable String feature,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String personId = userDetails.getUsername();
        boolean hasAccess = subscriptionService.hasFeatureAccess(personId, feature);
        
        return ResponseEntity.ok(ApiResponse.success(
                hasAccess ? "Accès autorisé" : "Accès refusé", hasAccess));
    }
}
