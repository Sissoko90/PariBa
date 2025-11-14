package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.GenerateToursRequest;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.TourResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.pariba.services.ITourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tours")
@Tag(name = "Tours de Rotation", description = "Gestion des tours de rotation dans les groupes")
@SecurityRequirement(name = "bearerAuth")
public class TourController {

    private final ITourService tourService;

    public TourController(ITourService tourService) {
        this.tourService = tourService;
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Générer les tours", description = "Génère automatiquement les tours de rotation pour un groupe. Nécessite d'être ADMIN du groupe.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tours générés"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<ApiResponse<List<TourResponse>>> generateTours(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody GenerateToursRequest request) {
        String personId = userDetails.getUsername();
        List<TourResponse> tours = tourService.generateTours(personId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.TOUR_SUCCESS_GENERATED, tours));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Détails d'un tour", description = "Récupère les détails d'un tour")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tour trouvé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tour non trouvé")
    })
    public ResponseEntity<ApiResponse<TourResponse>> getTourById(@PathVariable String id) {
        TourResponse tour = tourService.getTourById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, tour));
    }

    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Tours d'un groupe", description = "Récupère tous les tours d'un groupe")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des tours")
    })
    public ResponseEntity<ApiResponse<List<TourResponse>>> getToursByGroup(@PathVariable String groupId) {
        List<TourResponse> tours = tourService.getToursByGroup(groupId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, tours));
    }

    @GetMapping("/group/{groupId}/current")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Tour en cours", description = "Récupère le tour actuellement en cours pour un groupe")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tour en cours"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Aucun tour en cours")
    })
    public ResponseEntity<ApiResponse<TourResponse>> getCurrentTour(@PathVariable String groupId) {
        TourResponse tour = tourService.getCurrentTour(groupId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, tour));
    }

    @GetMapping("/group/{groupId}/next")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Prochain tour", description = "Récupère le prochain tour à venir pour un groupe")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Prochain tour"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Aucun tour suivant")
    })
    public ResponseEntity<ApiResponse<TourResponse>> getNextTour(@PathVariable String groupId) {
        TourResponse tour = tourService.getNextTour(groupId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, tour));
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Démarrer un tour", description = "Démarre un tour. Nécessite d'être ADMIN du groupe.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tour démarré"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<ApiResponse<Void>> startTour(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        String personId = userDetails.getUsername();
        tourService.startTour(id, personId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.TOUR_SUCCESS_STARTED, null));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Terminer un tour", description = "Marque un tour comme terminé. Nécessite d'être ADMIN du groupe.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tour terminé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<ApiResponse<Void>> completeTour(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        String personId = userDetails.getUsername();
        tourService.completeTour(id, personId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.TOUR_SUCCESS_COMPLETED, null));
    }
}
