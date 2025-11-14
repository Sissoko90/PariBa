package com.example.pariba.enums;

public enum ExportStatus { 
    PENDING,     // En attente
    QUEUED,      // En file d'attente
    PROCESSING,  // En cours de traitement
    RUNNING,     // En cours d'exécution
    DONE,        // Terminé
    COMPLETED,   // Complété (alias)
    ERROR,       // Erreur
    FAILED       // Échoué
}