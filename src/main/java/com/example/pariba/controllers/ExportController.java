package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.RequestExportRequest;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.ExportJobResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.pariba.services.IExportService;
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
@RequestMapping("/exports")
@Tag(name = "Exports", description = "Gestion des exports de données (PDF, Excel)")
@SecurityRequirement(name = "bearerAuth")
public class ExportController {

    private final IExportService exportService;

    public ExportController(IExportService exportService) {
        this.exportService = exportService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Demander un export", description = "Crée une demande d'export de données (contributions, paiements, etc.)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Export demandé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ApiResponse<ExportJobResponse>> requestExport(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RequestExportRequest request) {
        String personId = userDetails.getUsername();
        ExportJobResponse job = exportService.requestExport(personId, request);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.EXPORT_SUCCESS_REQUESTED, job));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Détails d'un export", description = "Récupère les détails et le statut d'un export")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Export trouvé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Export non trouvé")
    })
    public ResponseEntity<ApiResponse<ExportJobResponse>> getExportJob(@PathVariable String id) {
        ExportJobResponse job = exportService.getExportJobById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, job));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Mes exports", description = "Récupère tous les exports demandés par l'utilisateur connecté")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des exports")
    })
    public ResponseEntity<ApiResponse<List<ExportJobResponse>>> getMyExports(
            @AuthenticationPrincipal UserDetails userDetails) {
        String personId = userDetails.getUsername();
        List<ExportJobResponse> jobs = exportService.getExportJobsByPerson(personId);
        return ResponseEntity.ok(new ApiResponse<>(true, MessageConstants.SUCCESS_OPERATION, jobs));
    }
}
