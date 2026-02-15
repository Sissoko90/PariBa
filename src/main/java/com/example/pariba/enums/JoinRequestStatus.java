package com.example.pariba.enums;

public enum JoinRequestStatus {
    PENDING,    // En attente d'approbation
    APPROVED,   // Approuvée (la personne devient membre)
    REJECTED,   // Rejetée par l'admin
    CANCELLED   // Annulée par le demandeur
}
