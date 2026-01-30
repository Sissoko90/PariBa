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
    
    /**
     * Envoyer un email de réinitialisation avec token (pour admin dashboard)
     * 
     * @param email Email de l'administrateur
     */
    void sendResetPasswordEmail(String email);
    
    /**
     * Valider un token de réinitialisation
     * 
     * @param token Token de réinitialisation
     * @return true si le token est valide, false sinon
     */
    boolean validateResetToken(String token);
    
    /**
     * Réinitialiser le mot de passe avec un token (pour admin dashboard)
     * 
     * @param token Token de réinitialisation
     * @param newPassword Nouveau mot de passe
     * @return true si la réinitialisation a réussi, false sinon
     */
    boolean resetPassword(String token, String newPassword);
}
