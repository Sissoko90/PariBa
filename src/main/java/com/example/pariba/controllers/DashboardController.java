package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.DashboardSummaryResponse;
import com.example.pariba.security.CurrentUser;
import com.example.pariba.services.IDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour le dashboard mobile
 */
@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard Mobile", description = "Tableau de bord pour applications mobiles")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final IDashboardService dashboardService;
    private final CurrentUser currentUser;

    public DashboardController(IDashboardService dashboardService, CurrentUser currentUser) {
        this.dashboardService = dashboardService;
        this.currentUser = currentUser;
    }

    @GetMapping("/summary")
    @Operation(
        summary = "Résumé du dashboard",
        description = "Récupère un résumé des activités de l'utilisateur connecté"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Résumé récupéré"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboardSummary() {
        String personId = currentUser.getPersonId();
        DashboardSummaryResponse summary = dashboardService.getDashboardSummary(personId);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.SUCCESS_OPERATION, summary));
    }
}
