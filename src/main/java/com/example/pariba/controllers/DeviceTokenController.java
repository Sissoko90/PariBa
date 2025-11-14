package com.example.pariba.controllers;

import com.example.pariba.dtos.requests.RegisterDeviceRequest;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.DeviceTokenResponse;
import com.example.pariba.services.IDeviceTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des tokens d'appareil (push notifications)
 */
@RestController
@RequestMapping("/device-tokens")
@RequiredArgsConstructor
@Slf4j
public class DeviceTokenController {
    
    private final IDeviceTokenService deviceTokenService;
    
    /**
     * Enregistre ou met à jour un token d'appareil
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DeviceTokenResponse>> registerDeviceToken(
            @Valid @RequestBody RegisterDeviceRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("POST /device-tokens");
        
        String personId = userDetails.getUsername();
        DeviceTokenResponse response = deviceTokenService.registerDeviceToken(personId, request);
        
        return ResponseEntity.ok(ApiResponse.success("Token enregistré avec succès", response));
    }
    
    /**
     * Récupère tous les tokens actifs de l'utilisateur connecté
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DeviceTokenResponse>>> getActiveTokens(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("GET /device-tokens");
        
        String personId = userDetails.getUsername();
        List<DeviceTokenResponse> tokens = deviceTokenService.getActiveTokensByPerson(personId);
        
        return ResponseEntity.ok(ApiResponse.success("Tokens récupérés avec succès", tokens));
    }
    
    /**
     * Désactive un token d'appareil
     */
    @DeleteMapping("/{tokenId}")
    public ResponseEntity<ApiResponse<Void>> deactivateToken(
            @PathVariable String tokenId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("DELETE /device-tokens/{}", tokenId);
        
        String personId = userDetails.getUsername();
        deviceTokenService.deactivateToken(tokenId, personId);
        
        return ResponseEntity.ok(ApiResponse.success("Token désactivé avec succès", null));
    }
}
