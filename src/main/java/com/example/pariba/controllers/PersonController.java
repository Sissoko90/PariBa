package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.UpdateProfileRequest;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.PersonResponse;
import com.example.pariba.security.CurrentUser;
import com.example.pariba.services.IPersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Contrôleur pour la gestion des profils utilisateurs
 */
@RestController
@RequestMapping("/persons")
@Tag(name = "Profils", description = "Gestion des profils utilisateurs")
@SecurityRequirement(name = "bearerAuth")
public class PersonController {

    private final IPersonService personService;
    private final CurrentUser currentUser;

    public PersonController(IPersonService personService, CurrentUser currentUser) {
        this.personService = personService;
        this.currentUser = currentUser;
    }

    @GetMapping("/me")
    @Operation(
        summary = "Récupérer mon profil",
        description = "Récupère les informations du profil de l'utilisateur connecté"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profil récupéré"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<ApiResponse<PersonResponse>> getCurrentUser() {
        String personId = currentUser.getPersonId();
        PersonResponse response = personService.getPersonById(personId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_OPERATION, response));
    }

    @GetMapping("/{personId}")
    @Operation(
        summary = "Récupérer un profil par ID",
        description = "Récupère les informations publiques d'un utilisateur"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profil récupéré"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<ApiResponse<PersonResponse>> getPersonById(@PathVariable String personId) {
        PersonResponse response = personService.getPersonById(personId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_OPERATION, response));
    }

    @PutMapping("/me")
    @Operation(
        summary = "Mettre à jour mon profil",
        description = "Met à jour les informations du profil de l'utilisateur connecté"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profil mis à jour"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ApiResponse<PersonResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        String personId = currentUser.getPersonId();
        PersonResponse response = personService.updateProfile(personId, request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_PROFILE_UPDATED, response));
    }

    @PostMapping("/me/photo")
    @Operation(
        summary = "Uploader une photo de profil",
        description = "Upload une nouvelle photo de profil pour l'utilisateur connecté"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Photo uploadée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Fichier invalide")
    })
    public ResponseEntity<ApiResponse<PersonResponse>> uploadPhoto(@RequestParam("file") MultipartFile file) {
        String personId = currentUser.getPersonId();
        PersonResponse response = personService.uploadPhoto(personId, file);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_PHOTO_UPLOADED, response));
    }

    @DeleteMapping("/me/photo")
    @Operation(
        summary = "Supprimer la photo de profil",
        description = "Supprime la photo de profil de l'utilisateur connecté"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Photo supprimée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pas de photo")
    })
    public ResponseEntity<ApiResponse<String>> deletePhoto() {
        String personId = currentUser.getPersonId();
        personService.deletePhoto(personId);
        return ResponseEntity.ok(ApiResponse.success("Photo supprimée avec succès", null));
    }

    @DeleteMapping("/me")
    @Operation(
        summary = "Supprimer mon compte",
        description = "Supprime définitivement le compte de l'utilisateur connecté"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Compte supprimé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Erreur lors de la suppression")
    })
    public ResponseEntity<ApiResponse<String>> deleteAccount() {
        String personId = currentUser.getPersonId();
        personService.deleteAccount(personId);
        return ResponseEntity.ok(ApiResponse.success("Compte supprimé avec succès", null));
    }

    @GetMapping("/me/statistics")
    @Operation(
        summary = "Mes statistiques",
        description = "Récupère les statistiques personnelles de l'utilisateur connecté"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Statistiques récupérées"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<ApiResponse<com.example.pariba.dtos.responses.PersonalStatisticsResponse>> getMyStatistics() {
        String personId = currentUser.getPersonId();
        com.example.pariba.dtos.responses.PersonalStatisticsResponse stats = personService.getPersonalStatistics(personId);
        return ResponseEntity.ok(ApiResponse.success("Statistiques récupérées", stats));
    }
}
