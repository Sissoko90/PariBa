package com.example.pariba.controllers;

import com.example.pariba.dtos.requests.CreateJoinRequestRequest;
import com.example.pariba.dtos.requests.ReviewJoinRequestRequest;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.JoinRequestResponse;
import com.example.pariba.security.CurrentUser;
import com.example.pariba.services.IJoinRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/join-requests")
@Tag(name = "Join Requests", description = "Gestion des demandes d'adhésion aux groupes")
public class JoinRequestController {

    private final IJoinRequestService joinRequestService;
    private final CurrentUser currentUser;

    public JoinRequestController(IJoinRequestService joinRequestService, CurrentUser currentUser) {
        this.joinRequestService = joinRequestService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @Operation(
        summary = "Créer une demande d'adhésion",
        description = "Permet à un utilisateur de demander à rejoindre un groupe"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Demande créée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Demande invalide")
    })
    public ResponseEntity<ApiResponse<JoinRequestResponse>> createJoinRequest(
            @Valid @RequestBody CreateJoinRequestRequest request) {
        String personId = currentUser.getPersonId();
        JoinRequestResponse response = joinRequestService.createJoinRequest(personId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Demande d'adhésion envoyée avec succès", response));
    }

    @PutMapping("/{requestId}/review")
    @Operation(
        summary = "Approuver ou rejeter une demande",
        description = "Permet à un admin de groupe d'approuver ou rejeter une demande d'adhésion"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Demande traitée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<ApiResponse<JoinRequestResponse>> reviewJoinRequest(
            @PathVariable String requestId,
            @Valid @RequestBody ReviewJoinRequestRequest request) {
        String adminId = currentUser.getPersonId();
        JoinRequestResponse response = joinRequestService.reviewJoinRequest(requestId, adminId, request);
        return ResponseEntity.ok(ApiResponse.success("Demande traitée avec succès", response));
    }

    @DeleteMapping("/{requestId}")
    @Operation(
        summary = "Annuler une demande d'adhésion",
        description = "Permet à un utilisateur d'annuler sa propre demande en attente"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Demande annulée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<ApiResponse<Void>> cancelJoinRequest(@PathVariable String requestId) {
        String personId = currentUser.getPersonId();
        joinRequestService.cancelJoinRequest(requestId, personId);
        return ResponseEntity.ok(ApiResponse.success("Demande annulée avec succès", null));
    }

    @GetMapping("/group/{groupId}")
    @Operation(
        summary = "Récupérer les demandes d'un groupe",
        description = "Permet à un admin de voir toutes les demandes d'adhésion pour son groupe"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des demandes"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<ApiResponse<List<JoinRequestResponse>>> getGroupJoinRequests(
            @PathVariable String groupId) {
        String adminId = currentUser.getPersonId();
        List<JoinRequestResponse> response = joinRequestService.getGroupJoinRequests(groupId, adminId);
        return ResponseEntity.ok(ApiResponse.success("Liste des demandes récupérée", response));
    }

    @GetMapping("/my-requests")
    @Operation(
        summary = "Récupérer mes demandes d'adhésion",
        description = "Permet à un utilisateur de voir toutes ses demandes d'adhésion"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des demandes")
    })
    public ResponseEntity<ApiResponse<List<JoinRequestResponse>>> getMyJoinRequests() {
        String personId = currentUser.getPersonId();
        List<JoinRequestResponse> response = joinRequestService.getMyJoinRequests(personId);
        return ResponseEntity.ok(ApiResponse.success("Liste de vos demandes récupérée", response));
    }

    @GetMapping("/group/{groupId}/pending-count")
    @Operation(
        summary = "Compter les demandes en attente",
        description = "Permet à un admin de voir le nombre de demandes en attente pour son groupe"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Nombre de demandes"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<ApiResponse<Long>> countPendingJoinRequests(@PathVariable String groupId) {
        String adminId = currentUser.getPersonId();
        long count = joinRequestService.countPendingJoinRequests(groupId, adminId);
        return ResponseEntity.ok(ApiResponse.success("Nombre de demandes en attente", count));
    }
}
