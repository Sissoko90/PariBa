package com.example.pariba.controllers;

import com.example.pariba.constants.UiConstants;
import com.example.pariba.dtos.responses.AdvertisementResponse;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.services.IAdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des publicités
 */
@RestController
@RequestMapping("/advertisements")
@RequiredArgsConstructor
@Slf4j
public class AdvertisementController {
    
    private final IAdvertisementService advertisementService;
    
    /**
     * Récupère les publicités actives pour un placement donné
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdvertisementResponse>>> getActiveAdvertisements(
            @RequestParam String placement,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("GET /advertisements - placement: {}", placement);
        
        String personId = userDetails.getUsername();
        List<AdvertisementResponse> ads = advertisementService
                .getActiveAdvertisements(placement, personId);
        
        return ResponseEntity.ok(ApiResponse.success(UiConstants.SUCCESS_ADS_RETRIEVED, ads));
    }
    
    /**
     * Enregistre une impression de publicité
     */
    @PostMapping("/{adId}/impression")
    public ResponseEntity<ApiResponse<Void>> recordImpression(
            @PathVariable String adId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("POST /advertisements/{}/impression", adId);
        
        String personId = userDetails.getUsername();
        advertisementService.recordImpression(adId, personId);
        
        return ResponseEntity.ok(ApiResponse.success(UiConstants.SUCCESS_IMPRESSION_RECORDED, null));
    }
    
    /**
     * Enregistre un clic sur une publicité
     */
    @PostMapping("/{adId}/click")
    public ResponseEntity<ApiResponse<Void>> recordClick(
            @PathVariable String adId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("POST /advertisements/{}/click", adId);
        
        String personId = userDetails.getUsername();
        advertisementService.recordClick(adId, personId);
        
        return ResponseEntity.ok(ApiResponse.success(UiConstants.SUCCESS_CLICK_RECORDED, null));
    }
    
    /**
     * Récupère une publicité par son ID
     */
    @GetMapping("/{adId}")
    public ResponseEntity<ApiResponse<AdvertisementResponse>> getAdvertisementById(
            @PathVariable String adId) {
        
        log.info("GET /advertisements/{}", adId);
        
        AdvertisementResponse ad = advertisementService.getAdvertisementById(adId);
        
        return ResponseEntity.ok(ApiResponse.success(UiConstants.SUCCESS_AD_RETRIEVED, ad));
    }
}
