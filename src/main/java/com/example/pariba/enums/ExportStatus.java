package com.example.pariba.enums;

public enum ExportStatus { 
    PENDING("En attente"),
    QUEUED("En file d'attente"),
    PROCESSING("En cours de traitement"),
    RUNNING("En cours d'exécution"),
    DONE("Terminé"),
    COMPLETED("Complété"),
    ERROR("Erreur"),
    FAILED("Échoué");
    
    private final String label;
    
    ExportStatus(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}