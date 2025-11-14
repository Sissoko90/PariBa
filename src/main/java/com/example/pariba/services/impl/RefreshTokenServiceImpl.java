package com.example.pariba.services.impl;

import com.example.pariba.dtos.responses.AuthResponse;
import com.example.pariba.dtos.responses.PersonResponse;
import com.example.pariba.exceptions.UnauthorizedException;
import com.example.pariba.models.RefreshToken;
import com.example.pariba.models.User;
import com.example.pariba.repositories.RefreshTokenRepository;
import com.example.pariba.services.IRefreshTokenService;
import com.example.pariba.services.IJwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final IJwtService jwtService;
    
    // Durée de vie du refresh token : 30 jours
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 30L * 24 * 60 * 60 * 1000; // 30 jours

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
                                  IJwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user, String ipAddress, String userAgent) {
        // Limiter le nombre de refresh tokens actifs par utilisateur (max 5)
        long activeTokensCount = refreshTokenRepository.countByUserAndRevokedFalse(user);
        if (activeTokensCount >= 5) {
            // Révoquer les plus anciens tokens
            var activeTokens = refreshTokenRepository.findByUserAndRevokedFalse(user);
            activeTokens.stream()
                .sorted((t1, t2) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()))
                .limit(activeTokensCount - 4) // Garder seulement les 4 plus récents
                .forEach(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
        }

        // Créer le nouveau token
        String tokenValue = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_MS);
        
        RefreshToken refreshToken = new RefreshToken(tokenValue, user, expiryDate);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setUserAgent(userAgent);
        
        log.info("✅ Refresh token créé pour utilisateur: {}", user.getUsername());
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
            .orElseThrow(() -> new UnauthorizedException("Refresh token invalide"));

        // Vérifier si le token est révoqué
        if (refreshToken.isRevoked()) {
            log.warn("⚠️ Tentative d'utilisation d'un refresh token révoqué: {}", refreshTokenValue);
            throw new UnauthorizedException("Refresh token révoqué");
        }

        // Vérifier si le token est expiré
        if (refreshToken.isExpired()) {
            log.warn("⚠️ Tentative d'utilisation d'un refresh token expiré: {}", refreshTokenValue);
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new UnauthorizedException("Refresh token expiré");
        }

        User user = refreshToken.getUser();
        
        // Générer un nouveau JWT
        String newJwtToken = jwtService.generateToken(
            user.getPerson().getId(), 
            user.getPerson().getEmail(), 
            user.getPerson().getRole()
        );

        log.info("✅ Token rafraîchi pour utilisateur: {}", user.getUsername());
        
        return new AuthResponse(newJwtToken, new PersonResponse(user.getPerson()));
    }

    @Override
    @Transactional
    public void revokeToken(String refreshTokenValue) {
        refreshTokenRepository.revokeToken(refreshTokenValue);
        log.info("🔒 Refresh token révoqué: {}", refreshTokenValue);
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(String userId) {
        refreshTokenRepository.revokeAllUserTokens(userId);
        log.info("🔒 Tous les refresh tokens révoqués pour utilisateur: {}", userId);
    }

    @Override
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredAndRevokedTokens(Instant.now());
        log.info("🧹 Nettoyage des refresh tokens expirés effectué");
    }

    @Override
    public boolean isTokenValid(String refreshTokenValue) {
        return refreshTokenRepository.findByToken(refreshTokenValue)
            .map(token -> !token.isRevoked() && !token.isExpired())
            .orElse(false);
    }
}
