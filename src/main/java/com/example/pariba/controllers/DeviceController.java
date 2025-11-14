package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.RegisterDeviceRequest;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.DeviceResponse;
import com.example.pariba.security.CurrentUser;
import com.example.pariba.services.IDeviceTokenService;
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
 * Contrôleur pour la gestion des appareils mobiles
 */
@RestController
@RequestMapping("/devices")
@Tag(name = "Appareils", description = "Gestion des appareils mobiles et tokens de notification")
@SecurityRequirement(name = "bearerAuth")
public class DeviceController {

    private final IDeviceTokenService deviceTokenService;
    private final CurrentUser currentUser;

    public DeviceController(IDeviceTokenService deviceTokenService, CurrentUser currentUser) {
        this.deviceTokenService = deviceTokenService;
        this.currentUser = currentUser;
    }

    @PostMapping("/register")
    @Operation(
        summary = "Enregistrer un appareil",
        description = "Enregistre un nouvel appareil mobile avec son token de notification"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Appareil enregistré"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Appareil déjà enregistré")
    })
    public ResponseEntity<ApiResponse<DeviceResponse>> registerDevice(@Valid @RequestBody RegisterDeviceRequest request) {
        String personId = currentUser.getPersonId();
        DeviceResponse response = deviceTokenService.registerDevice(personId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appareil enregistré avec succès", response));
    }

    @GetMapping("/me")
    @Operation(
        summary = "Mes appareils",
        description = "Récupère la liste de tous mes appareils enregistrés"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des appareils")
    })
    public ResponseEntity<ApiResponse<List<DeviceResponse>>> getMyDevices() {
        String personId = currentUser.getPersonId();
        List<DeviceResponse> devices = deviceTokenService.getDevicesByPerson(personId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_OPERATION, devices));
    }

    @PutMapping("/{deviceId}/activate")
    @Operation(
        summary = "Activer un appareil",
        description = "Active un appareil désactivé"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Appareil activé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appareil non trouvé")
    })
    public ResponseEntity<ApiResponse<DeviceResponse>> activateDevice(@PathVariable String deviceId) {
        String personId = currentUser.getPersonId();
        DeviceResponse response = deviceTokenService.activateDevice(personId, deviceId);
        return ResponseEntity.ok(ApiResponse.success("Appareil activé avec succès", response));
    }

    @PutMapping("/{deviceId}/deactivate")
    @Operation(
        summary = "Désactiver un appareil",
        description = "Désactive un appareil (arrête les notifications)"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Appareil désactivé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appareil non trouvé")
    })
    public ResponseEntity<ApiResponse<DeviceResponse>> deactivateDevice(@PathVariable String deviceId) {
        String personId = currentUser.getPersonId();
        DeviceResponse response = deviceTokenService.deactivateDevice(personId, deviceId);
        return ResponseEntity.ok(ApiResponse.success("Appareil désactivé avec succès", response));
    }

    @DeleteMapping("/{deviceId}")
    @Operation(
        summary = "Supprimer un appareil",
        description = "Supprime définitivement un appareil de la liste"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Appareil supprimé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appareil non trouvé")
    })
    public ResponseEntity<ApiResponse<String>> deleteDevice(@PathVariable String deviceId) {
        String personId = currentUser.getPersonId();
        deviceTokenService.deleteDevice(personId, deviceId);
        return ResponseEntity.ok(ApiResponse.success("Appareil supprimé avec succès", null));
    }

    @PutMapping("/{deviceId}/update-token")
    @Operation(
        summary = "Mettre à jour le token",
        description = "Met à jour le token de notification d'un appareil"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token mis à jour"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Appareil non trouvé")
    })
    public ResponseEntity<ApiResponse<DeviceResponse>> updateToken(
            @PathVariable String deviceId, 
            @RequestParam String newToken) {
        String personId = currentUser.getPersonId();
        DeviceResponse response = deviceTokenService.updateToken(personId, deviceId, newToken);
        return ResponseEntity.ok(ApiResponse.success("Token mis à jour avec succès", response));
    }
}
