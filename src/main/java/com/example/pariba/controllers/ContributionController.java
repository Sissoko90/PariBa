package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.ContributionResponse;
import com.example.pariba.services.IContributionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contributions")
@Tag(name = "Contributions", description = "Gestion des contributions (cotisations) des membres")
@SecurityRequirement(name = "bearerAuth")
public class ContributionController {

    private final IContributionService contributionService;

    public ContributionController(IContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Détails d'une contribution", description = "Récupère les détails d'une contribution")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Contribution trouvée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Contribution non trouvée")
    })
    public ResponseEntity<ApiResponse<ContributionResponse>> getContributionById(@PathVariable String id) {
        ContributionResponse contribution = contributionService.getContributionById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, contribution));
    }

    @GetMapping("/tour/{tourId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Contributions d'un tour", description = "Récupère toutes les contributions d'un tour")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des contributions")
    })
    public ResponseEntity<ApiResponse<List<ContributionResponse>>> getContributionsByTour(@PathVariable String tourId) {
        List<ContributionResponse> contributions = contributionService.getContributionsByTour(tourId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, contributions));
    }

    @GetMapping("/member/{personId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Contributions d'un membre", description = "Récupère toutes les contributions d'un membre")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des contributions")
    })
    public ResponseEntity<ApiResponse<List<ContributionResponse>>> getContributionsByMember(@PathVariable String personId) {
        List<ContributionResponse> contributions = contributionService.getContributionsByMember(personId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, contributions));
    }

    @GetMapping("/group/{groupId}/pending")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Contributions en attente", description = "Récupère toutes les contributions en attente de paiement pour un groupe")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des contributions en attente")
    })
    public ResponseEntity<ApiResponse<List<ContributionResponse>>> getPendingContributions(@PathVariable String groupId) {
        List<ContributionResponse> contributions = contributionService.getPendingContributions(groupId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, contributions));
    }
}
