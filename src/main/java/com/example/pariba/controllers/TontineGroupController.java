package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.CreateGroupRequest;
import com.example.pariba.dtos.requests.UpdateGroupRequest;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.GroupResponse;
import com.example.pariba.dtos.responses.GroupShareLinkResponse;
import com.example.pariba.security.CurrentUser;
import com.example.pariba.services.ITontineGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour la gestion des groupes de tontine
 */
@RestController
@RequestMapping("/groups")
@Tag(name = "Groupes de Tontine", description = "Gestion des groupes de tontine (création, modification, suppression)")
@SecurityRequirement(name = "bearerAuth")
public class TontineGroupController {

    private final ITontineGroupService groupService;
    private final CurrentUser currentUser;

    public TontineGroupController(ITontineGroupService groupService, CurrentUser currentUser) {
        this.groupService = groupService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @Operation(
        summary = "Créer un groupe de tontine",
        description = "Crée un nouveau groupe de tontine. L'utilisateur connecté devient automatiquement ADMIN du groupe."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Groupe créé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        String creatorId = currentUser.getPersonId();
        GroupResponse response = groupService.createGroup(creatorId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(MessageConstants.SUCCESS_GROUP_CREATED, response));
    }

    @GetMapping("/{groupId}")
    @Operation(
        summary = "Récupérer un groupe par ID",
        description = "Récupère les détails d'un groupe de tontine"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Groupe récupéré"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Groupe non trouvé")
    })
    public ResponseEntity<ApiResponse<GroupResponse>> getGroupById(@PathVariable String groupId) {
        String personId = currentUser.getPersonId();
        GroupResponse response = groupService.getGroupById(groupId, personId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_OPERATION, response));
    }

    @GetMapping("/my-groups")
    @Operation(
        summary = "Récupérer mes groupes",
        description = "Récupère tous les groupes dont l'utilisateur connecté est membre"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des groupes")
    })
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getMyGroups() {
        String personId = currentUser.getPersonId();
        List<GroupResponse> response = groupService.getGroupsByPerson(personId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_OPERATION, response));
    }

    @GetMapping("/created-by-me")
    @Operation(
        summary = "Récupérer les groupes que j'ai créés",
        description = "Récupère tous les groupes créés par l'utilisateur connecté"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des groupes créés")
    })
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getGroupsCreatedByMe() {
        String personId = currentUser.getPersonId();
        List<GroupResponse> response = groupService.getGroupsCreatedByPerson(personId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_OPERATION, response));
    }

    @PutMapping("/{groupId}")
    @Operation(
        summary = "Mettre à jour un groupe",
        description = "Met à jour les informations d'un groupe. Nécessite d'être ADMIN du groupe."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Groupe mis à jour"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé (pas admin du groupe)"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Groupe non trouvé")
    })
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(
            @PathVariable String groupId,
            @Valid @RequestBody UpdateGroupRequest request) {
        String personId = currentUser.getPersonId();
        GroupResponse response = groupService.updateGroup(groupId, personId, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_GROUP_UPDATED, response));
    }

    @DeleteMapping("/{groupId}")
    @Operation(
        summary = "Supprimer un groupe",
        description = "Supprime un groupe de tontine. Nécessite d'être ADMIN du groupe."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Groupe supprimé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Groupe non trouvé")
    })
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable String groupId) {
        String personId = currentUser.getPersonId();
        groupService.deleteGroup(groupId, personId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_GROUP_DELETED, null));
    }

    @GetMapping("/{groupId}/share-link")
    @Operation(
        summary = "Générer un lien de partage",
        description = "Génère un lien de partage pour inviter des personnes à rejoindre le groupe"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lien généré"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Non autorisé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Groupe non trouvé")
    })
    public ResponseEntity<ApiResponse<GroupShareLinkResponse>> generateShareLink(@PathVariable String groupId) {
        String personId = currentUser.getPersonId();
        GroupShareLinkResponse response = groupService.generateShareLink(groupId, personId);
        return ResponseEntity.ok(ApiResponse.success("Lien de partage généré", response));
    }

    @PostMapping("/{groupId}/leave")
    @Operation(
        summary = "Quitter un groupe",
        description = "Permet à un membre de quitter un groupe de tontine. L'ADMIN ne peut pas quitter son propre groupe."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Groupe quitté"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Impossible de quitter"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Groupe non trouvé")
    })
    public ResponseEntity<ApiResponse<String>> leaveGroup(@PathVariable String groupId) {
        String personId = currentUser.getPersonId();
        groupService.leaveGroup(groupId, personId);
        return ResponseEntity.ok(ApiResponse.success("Vous avez quitté le groupe avec succès", null));
    }
}
