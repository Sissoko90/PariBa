package com.example.pariba.enums;

public enum AdPlacement { 
    HOME_TOP("Haut de page d'accueil"), 
    HOME_LIST("Liste d'accueil"), 
    GROUP_HEADER("En-tête de groupe"), 
    HISTORY_BANNER("Bannière historique");
    
    private final String label;
    
    AdPlacement(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}