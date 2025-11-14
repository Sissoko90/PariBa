package com.example.pariba.services;

/**
 * Service pour la récupération de mot de passe
 */
public interface IPasswordResetService {
    
    /**
     * Envoyer un code OTP pour réinitialiser le mot de passe
     * Détecte automatiquement si c'est un email ou un téléphone
     * 
     * @param identifier Email ou numéro de téléphone
     * @return Code OTP (en dev uniquement)
     */
    String sendPasswordResetOtp(String identifier);
    
    /**
     * Vérifier le code OTP et réinitialiser le mot de passe
     * 
     * @param identifier Email ou numéro de téléphone
     * @param otpCode Code OTP reçu
     * @param newPassword Nouveau mot de passe
     */
    void resetPassword(String identifier, String otpCode, String newPassword);
    
    /**
     * Changer le mot de passe (utilisateur connecté)
     * 
     * @param personId ID de la personne
     * @param oldPassword Ancien mot de passe
     * @param newPassword Nouveau mot de passe
     */
    void changePassword(String personId, String oldPassword, String newPassword);
}
