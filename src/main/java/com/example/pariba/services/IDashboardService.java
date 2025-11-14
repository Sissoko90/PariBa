package com.example.pariba.services;

import com.example.pariba.dtos.responses.DashboardSummaryResponse;

public interface IDashboardService {
    
    /**
     * Récupère le résumé du dashboard pour un utilisateur
     */
    DashboardSummaryResponse getDashboardSummary(String personId);
}
