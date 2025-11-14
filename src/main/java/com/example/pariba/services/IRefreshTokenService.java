package com.example.pariba.services;

import com.example.pariba.dtos.responses.AuthResponse;
import com.example.pariba.models.RefreshToken;
import com.example.pariba.models.User;

public interface IRefreshTokenService {
    
    /**
     * Crée un nouveau refresh token pour un utilisateur
     */
    RefreshToken createRefreshToken(User user, String ipAddress, String userAgent);
    
    /**
     * Valide et rafraîchit un token
     */
    AuthResponse refreshToken(String refreshToken);
    
    /**
     * Révoque un refresh token
     */
    void revokeToken(String refreshToken);
    
    /**
     * Révoque tous les tokens d'un utilisateur
     */
    void revokeAllUserTokens(String userId);
    
    /**
     * Nettoie les tokens expirés et révoqués
     */
    void cleanupExpiredTokens();
    
    /**
     * Vérifie si un token est valide
     */
    boolean isTokenValid(String refreshToken);
}
