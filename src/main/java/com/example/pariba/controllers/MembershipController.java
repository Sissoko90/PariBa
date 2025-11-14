package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.UpdateMemberRoleRequest;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.MembershipResponse;
import com.example.pariba.security.CurrentUser;
import com.example.pariba.services.IMembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/memberships")
@Tag(name = "Membres de Groupe", description = "Gestion des membres et de leurs rôles dans les groupes")
@SecurityRequirement(name = "bearerAuth")
public class MembershipController {

    private final IMembershipService membershipService;
    private final CurrentUser currentUser;

    public MembershipController(IMembershipService membershipService, CurrentUser currentUser) {
        this.membershipService = membershipService;
        this.currentUser = currentUser;
    }

    @GetMapping("/group/{groupId}")
    @Operation(summary = "Liste des membres d'un groupe", description = "Récupère tous les membres d'un groupe avec leurs rôles")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des membres"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Groupe non trouvé")
    })
    public ResponseEntity<ApiResponse<List<MembershipResponse>>> getMembersByGroup(@PathVariable String groupId) {
        List<MembershipResponse> response = membershipService.getMembersByGroup(groupId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_OPERATION, response));
    }

    @GetMapping("/group/{groupId}/person/{personId}")
    @Operation(summary = "Obtenir un membre spécifique", description = "Récupère les informations d'un membre dans un groupe")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Membre trouvé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Membre non trouvé")
    })
    public ResponseEntity<ApiResponse<MembershipResponse>> getMemberByGroupAndPerson(
            @PathVariable String groupId,
            @PathVariable String personId) {
        MembershipResponse response = membershipService.getMemberByGroupAndPerson(groupId, personId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_OPERATION, response));
    }

    @GetMapping("/my-memberships")
    @Operation(summary = "Mes appartenances", description = "Récupère tous les groupes dont je suis membre avec mon rôle")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des appartenances")
    })
    public ResponseEntity<ApiResponse<List<MembershipResponse>>> getMyMemberships() {
        String personId = currentUser.getPersonId();
        List<MembershipResponse> response = membershipService.getGroupsByPerson(personId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_OPERATION, response));
    }

    @PutMapping("/role")
    @Operation(summary = "Modifier le rôle d'un membre", description = "Change le rôle d'un membre dans un groupe. Nécessite d'être ADMIN du groupe.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rôle mis à jour"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<ApiResponse<MembershipResponse>> updateMemberRole(@Valid @RequestBody UpdateMemberRoleRequest request) {
        String requesterId = currentUser.getPersonId();
        MembershipResponse response = membershipService.updateMemberRole(requesterId, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_MEMBER_ROLE_UPDATED, response));
    }

    @PutMapping("/group/{groupId}/person/{personId}/promote")
    @Operation(summary = "Promouvoir un membre en ADMIN", description = "Promouvoir un membre au rôle ADMIN. Nécessite d'être ADMIN du groupe.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Membre promu"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<ApiResponse<MembershipResponse>> promoteMember(
            @PathVariable String groupId,
            @PathVariable String personId) {
        String requesterId = currentUser.getPersonId();
        MembershipResponse response = membershipService.promoteMemberToAdmin(groupId, personId, requesterId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_MEMBER_ROLE_UPDATED, response));
    }

    @PutMapping("/group/{groupId}/person/{personId}/demote")
    @Operation(summary = "Rétrograder un ADMIN en membre", description = "Rétrograder un ADMIN au rôle MEMBER. Nécessite d'être ADMIN du groupe.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Admin rétrogradé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<ApiResponse<MembershipResponse>> demoteMember(
            @PathVariable String groupId,
            @PathVariable String personId) {
        String requesterId = currentUser.getPersonId();
        MembershipResponse response = membershipService.demoteAdminToMember(groupId, personId, requesterId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_MEMBER_ROLE_UPDATED, response));
    }

    @DeleteMapping("/group/{groupId}/member/{personId}")
    @Operation(summary = "Retirer un membre", description = "Retire un membre d'un groupe. Nécessite d'être ADMIN du groupe.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Membre retiré"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable String groupId,
            @PathVariable String personId) {
        String requesterId = currentUser.getPersonId();
        membershipService.removeMember(groupId, personId, requesterId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_MEMBER_REMOVED, null));
    }
}
