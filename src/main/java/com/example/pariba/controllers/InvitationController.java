package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.AcceptInvitationRequest;
import com.example.pariba.dtos.requests.InviteMemberRequest;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.InvitationResponse;
import com.example.pariba.security.CurrentUser;
import com.example.pariba.services.IInvitationService;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.ISystemLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invitations")
@Tag(name = "Invitations", description = "Système d'invitation de membres dans les groupes")
@SecurityRequirement(name = "bearerAuth")
public class InvitationController {

    private final IInvitationService invitationService;
    private final CurrentUser currentUser;
    private final IAuditService auditService;
    private final ISystemLogService systemLogService;

    public InvitationController(IInvitationService invitationService, CurrentUser currentUser,
                                IAuditService auditService, ISystemLogService systemLogService) {
        this.invitationService = invitationService;
        this.currentUser = currentUser;
        this.auditService = auditService;
        this.systemLogService = systemLogService;
    }

    @PostMapping
    @Operation(summary = "Inviter un membre", description = "Envoie une invitation pour rejoindre un groupe. Nécessite d'être ADMIN du groupe.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Invitation envoyée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<ApiResponse<InvitationResponse>> inviteMember(@Valid @RequestBody InviteMemberRequest request) {
        String inviterId = currentUser.getPersonId();
        InvitationResponse response = invitationService.inviteMember(inviterId, request);
        
        String details = String.format("{\"groupId\":\"%s\",\"targetPhone\":\"%s\"}", request.getGroupId(), request.getTargetPhone());
        auditService.log(inviterId, "INVITATION_SENT", "Invitation", response.getId(), details);
        systemLogService.log(inviterId, "User", "INVITATION_SENT", "Invitation", response.getId(), details, "INFO", true);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.SUCCESS_INVITATION_SENT, response));
    }

    @PostMapping("/accept")
    @Operation(summary = "Accepter une invitation", description = "Accepte une invitation et rejoint le groupe")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Invitation acceptée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Invitation non trouvée ou expirée")
    })
    public ResponseEntity<ApiResponse<Void>> acceptInvitation(@Valid @RequestBody AcceptInvitationRequest request) {
        String personId = currentUser.getPersonId();
        invitationService.acceptInvitation(personId, request.getLinkCode());
        
        String details = String.format("{\"linkCode\":\"%s\"}", request.getLinkCode());
        auditService.log(personId, "INVITATION_ACCEPTED", "Invitation", null, details);
        systemLogService.log(personId, "User", "INVITATION_ACCEPTED", "Invitation", null, details, "INFO", true);
        
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_INVITATION_ACCEPTED, null));
    }

    @GetMapping("/group/{groupId}")
    @Operation(summary = "Invitations d'un groupe", description = "Récupère toutes les invitations d'un groupe")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des invitations")
    })
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getInvitationsByGroup(@PathVariable String groupId) {
        List<InvitationResponse> response = invitationService.getInvitationsByGroup(groupId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_OPERATION, response));
    }
}
