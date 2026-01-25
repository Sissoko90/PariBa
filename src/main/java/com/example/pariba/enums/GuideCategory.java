package com.example.pariba.enums;

public enum GuideCategory {
    GETTING_STARTED("Démarrage"),
    ACCOUNT_MANAGEMENT("Gestion du compte"),
    TONTINE_CREATION("Création de tontine"),
    TONTINE_PARTICIPATION("Participation aux tontines"),
    PAYMENTS("Paiements"),
    NOTIFICATIONS("Notifications"),
    SECURITY("Sécurité"),
    TROUBLESHOOTING("Dépannage"),
    OTHER("Autre");
    
    private final String label;
    
    GuideCategory(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}
