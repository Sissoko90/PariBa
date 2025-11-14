package com.example.pariba.controllers;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.ChangePasswordRequest;
import com.example.pariba.dtos.requests.ForgotPasswordRequest;
import com.example.pariba.dtos.requests.LoginRequest;
import com.example.pariba.dtos.requests.RefreshTokenRequest;
import com.example.pariba.dtos.requests.RegisterRequest;
import com.example.pariba.dtos.requests.ResetPasswordRequest;
import com.example.pariba.dtos.requests.SendOtpRequest;
import com.example.pariba.dtos.requests.OtpVerifyRequest;
import com.example.pariba.dtos.responses.ApiResponse;
import com.example.pariba.dtos.responses.AuthResponse;
import com.example.pariba.security.CurrentUser;
import com.example.pariba.services.IAuthService;
import com.example.pariba.services.IOtpService;
import com.example.pariba.services.IPasswordResetService;
import com.example.pariba.services.IRefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour l'authentification et la gestion des comptes
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentification", description = "Endpoints pour l'inscription, la connexion et la gestion des comptes")
public class AuthController {

    private final IAuthService authService;
    private final IOtpService otpService;
    private final IPasswordResetService passwordResetService;
    private final IRefreshTokenService refreshTokenService;
    private final CurrentUser currentUser;

    public AuthController(IAuthService authService, 
                         IOtpService otpService,
                         IPasswordResetService passwordResetService,
                         IRefreshTokenService refreshTokenService,
                         CurrentUser currentUser) {
        this.authService = authService;
        this.otpService = otpService;
        this.passwordResetService = passwordResetService;
        this.refreshTokenService = refreshTokenService;
        this.currentUser = currentUser;
    }

    @PostMapping("/register")
    @Operation(
        summary = "Inscription d'un nouvel utilisateur",
        description = "Crée un nouveau compte utilisateur avec validation OTP optionnelle"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inscription réussie"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Utilisateur déjà existant")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.AUTH_SUCCESS_REGISTER, response));
    }

    @PostMapping("/login")
    @Operation(
        summary = "Connexion utilisateur",
        description = "Authentifie un utilisateur et retourne un token JWT"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Connexion réussie"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Identifiants invalides")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.AUTH_SUCCESS_LOGIN, response));
    }

    @PostMapping("/otp/send")
    @Operation(
        summary = "Envoyer un code OTP",
        description = "Génère et envoie un code OTP par SMS ou email pour vérification"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP envoyé avec succès"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cible invalide")
    })
    public ResponseEntity<ApiResponse<String>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        // Convertir le canal string en enum si fourni
        com.example.pariba.enums.NotificationChannel channel = null;
        if (request.getChannel() != null && !request.getChannel().isBlank()) {
            try {
                channel = com.example.pariba.enums.NotificationChannel.valueOf(request.getChannel().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new com.example.pariba.exceptions.BadRequestException(
                    "Canal invalide. Valeurs acceptées: EMAIL, SMS, WHATSAPP"
                );
            }
        }
        
        String code = otpService.generateAndSendOtp(request.getTarget(), channel);
        // En production, ne pas retourner le code
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.OTP_SUCCESS_SENT, code));
    }

    @PostMapping("/otp/verify")
    @Operation(
        summary = "Vérifier un code OTP",
        description = "Vérifie la validité d'un code OTP"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vérification effectuée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Code invalide ou expiré")
    })
    public ResponseEntity<ApiResponse<Boolean>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        boolean verified = otpService.verifyOtp(request.getTarget(), request.getCode());
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.OTP_SUCCESS_VERIFIED, verified));
    }

    @PostMapping("/password/forgot")
    @Operation(
        summary = "Mot de passe oublié",
        description = "Envoie un code OTP par email ou SMS pour réinitialiser le mot de passe"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Code OTP envoyé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String otpCode = passwordResetService.sendPasswordResetOtp(request.getPhone());
        // En production, ne pas retourner le code
        return ResponseEntity.ok(ApiResponse.success("Code OTP envoyé avec succès", otpCode));
    }

    @PostMapping("/password/reset")
    @Operation(
        summary = "Réinitialiser le mot de passe",
        description = "Réinitialise le mot de passe après vérification du code OTP"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Code OTP invalide")
    })
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getTarget(), request.getOtpCode(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Mot de passe réinitialisé avec succès", null));
    }

    @PostMapping("/password/change")
    @Operation(
        summary = "Changer le mot de passe",
        description = "Change le mot de passe de l'utilisateur connecté"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Mot de passe changé"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ancien mot de passe incorrect")
    })
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String personId = currentUser.getPersonId();
        passwordResetService.changePassword(personId, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Mot de passe changé avec succès", null));
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Rafraîchir le token JWT",
        description = "Génère un nouveau token JWT à partir d'un refresh token valide"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token rafraîchi"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token invalide ou expiré")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = refreshTokenService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Token rafraîchi avec succès", response));
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Déconnexion",
        description = "Révoque le refresh token et déconnecte l'utilisateur"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Déconnexion réussie"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Token invalide")
    })
    public ResponseEntity<ApiResponse<String>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Déconnexion réussie", null));
    }

    @GetMapping("/validate")
    @Operation(
        summary = "Valider un token",
        description = "Vérifie la validité d'un refresh token"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Validation effectuée"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Token invalide")
    })
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestParam String refreshToken) {
        boolean isValid = refreshTokenService.isTokenValid(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Token validé", isValid));
    }
}
