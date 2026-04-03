package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.CreateDelegationRequest;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.DelegationResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.pariba.services.IDelegationService;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.ISystemLogService;
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
@RequestMapping("/delegations")
@Tag(name = "Délégations", description = "Système de délégation de pouvoirs entre membres")
@SecurityRequirement(name = "bearerAuth")
public class DelegationController {

    private final IDelegationService delegationService;
    private final IAuditService auditService;
    private final ISystemLogService systemLogService;

    public DelegationController(IDelegationService delegationService, IAuditService auditService, ISystemLogService systemLogService) {
        this.delegationService = delegationService;
        this.auditService = auditService;
        this.systemLogService = systemLogService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Créer une délégation", description = "Délègue ses pouvoirs à un autre membre du groupe")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Délégation créée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ApiResponse<DelegationResponse>> createDelegation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateDelegationRequest request) {
        String personId = userDetails.getUsername();
        DelegationResponse delegation = delegationService.createDelegation(personId, request);
        
        String details = String.format("{\"delegationId\":\"%s\",\"groupId\":\"%s\"}", 
            delegation.getId(), request.getGroupId());
        auditService.log(personId, "DELEGATION_CREATED", "Delegation", delegation.getId(), details);
        systemLogService.log(personId, "User", "DELEGATION_CREATED", "Delegation", delegation.getId(), details, "INFO", true);
        
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.DELEGATION_SUCCESS_CREATED, delegation));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Détails d'une délégation", description = "Récupère les détails d'une délégation")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Délégation trouvée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Délégation non trouvée")
    })
    public ResponseEntity<ApiResponse<DelegationResponse>> getDelegationById(@PathVariable String id) {
        DelegationResponse delegation = delegationService.getDelegationById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, delegation));
    }

    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Délégations d'un groupe", description = "Récupère toutes les délégations d'un groupe")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des délégations")
    })
    public ResponseEntity<ApiResponse<List<DelegationResponse>>> getDelegationsByGroup(@PathVariable String groupId) {
        List<DelegationResponse> delegations = delegationService.getDelegationsByGroup(groupId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, delegations));
    }

    @GetMapping("/group/{groupId}/active")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Délégations actives", description = "Récupère les délégations actuellement actives d'un groupe")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des délégations actives")
    })
    public ResponseEntity<ApiResponse<List<DelegationResponse>>> getActiveDelegations(@PathVariable String groupId) {
        List<DelegationResponse> delegations = delegationService.getActiveDelegations(groupId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, delegations));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Révoquer une délégation", description = "Révoque une délégation existante")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Délégation révoquée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Délégation non trouvée")
    })
    public ResponseEntity<ApiResponse<Void>> revokeDelegation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) {
        String personId = userDetails.getUsername();
        String details = String.format("{\"delegationId\":\"%s\"}", id);
        auditService.log(personId, "DELEGATION_REVOKED", "Delegation", id, details);
        systemLogService.log(personId, "User", "DELEGATION_REVOKED", "Delegation", id, details, "WARNING", true);
        
        delegationService.revokeDelegation(id, personId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.DELEGATION_SUCCESS_REVOKED, null));
    }
}
