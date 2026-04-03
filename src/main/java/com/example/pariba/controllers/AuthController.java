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
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.ISystemLogService;
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
    private final IAuditService auditService;
    private final ISystemLogService systemLogService;

    public AuthController(IAuthService authService, 
                         IOtpService otpService,
                         IPasswordResetService passwordResetService,
                         IRefreshTokenService refreshTokenService,
                         CurrentUser currentUser,
                         IAuditService auditService,
                         ISystemLogService systemLogService) {
        this.authService = authService;
        this.otpService = otpService;
        this.passwordResetService = passwordResetService;
        this.refreshTokenService = refreshTokenService;
        this.currentUser = currentUser;
        this.auditService = auditService;
        this.systemLogService = systemLogService;
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
        
        // Logs
        String userId = response.getPerson() != null ? response.getPerson().getId() : null;
        String userName = request.getPrenom() + " " + request.getNom();
        String details = String.format("{\"phone\":\"%s\",\"email\":\"%s\"}", request.getPhone(), request.getEmail());
        if (userId != null) {
            auditService.log(userId, "USER_REGISTERED", "Person", userId, details);
            systemLogService.log(userId, userName, "USER_REGISTERED", "Person", userId, details, "INFO", true);
        }
        
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
        String details = String.format("{\"username\":\"%s\"}", request.getUsername());
        try {
            AuthResponse response = authService.login(request);
            
            // Logs succès
            String userId = response.getPerson() != null ? response.getPerson().getId() : null;
            String userName = response.getPerson() != null ? response.getPerson().getNom() : request.getUsername();
            if (userId != null) {
                auditService.log(userId, "USER_LOGIN_SUCCESS", "Person", userId, details);
                systemLogService.log(userId, userName, "USER_LOGIN_SUCCESS", "Person", userId, details, "INFO", true);
            }
            
            return ResponseEntity.ok(ApiResponse.success(MessageConstants.AUTH_SUCCESS_LOGIN, response));
        } catch (Exception e) {
            // Log échec de connexion
            systemLogService.log(null, request.getUsername(), "USER_LOGIN_FAILED", "Person", null, 
                String.format("{\"username\":\"%s\",\"reason\":\"%s\"}", request.getUsername(), e.getMessage()), "WARNING", false);
            throw e;
        }
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
                systemLogService.log(null, request.getTarget(), "OTP_SEND_FAILED", "OTP", null, 
                    String.format("{\"target\":\"%s\",\"reason\":\"Canal invalide\"}", request.getTarget()), "WARNING", false);
                throw new com.example.pariba.exceptions.BadRequestException(
                    "Canal invalide. Valeurs acceptées: EMAIL, SMS, WHATSAPP"
                );
            }
        }
        
        otpService.generateAndSendOtp(request.getTarget(), channel);
        
        // Log envoi OTP
        String details = String.format("{\"target\":\"%s\",\"channel\":\"%s\"}", request.getTarget(), channel);
        systemLogService.log(null, request.getTarget(), "OTP_SENT", "OTP", null, details, "INFO", true);
        
        // Ne jamais retourner le code OTP dans la reponse (securite)
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.OTP_SUCCESS_SENT, null));
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
        
        // Log vérification OTP
        String details = String.format("{\"target\":\"%s\",\"verified\":%s}", request.getTarget(), verified);
        if (verified) {
            systemLogService.log(null, request.getTarget(), "OTP_VERIFIED_SUCCESS", "OTP", null, details, "INFO", true);
        } else {
            systemLogService.log(null, request.getTarget(), "OTP_VERIFIED_FAILED", "OTP", null, details, "WARNING", false);
        }
        
        return ResponseEntity.ok(ApiResponse.success(MessageConstants.OTP_SUCCESS_VERIFIED, verified));
    }

    @PostMapping("/otp/resend")
    @Operation(
        summary = "Renvoyer un code OTP",
        description = "Regenere et renvoie un nouveau code OTP par SMS ou email"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP renvoye avec succes"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cible invalide"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Trop de tentatives, veuillez patienter")
    })
    public ResponseEntity<ApiResponse<String>> resendOtp(@Valid @RequestBody SendOtpRequest request) {
        // Convertir le canal string en enum si fourni
        com.example.pariba.enums.NotificationChannel channel = null;
        if (request.getChannel() != null && !request.getChannel().isBlank()) {
            try {
                channel = com.example.pariba.enums.NotificationChannel.valueOf(request.getChannel().toUpperCase());
            } catch (IllegalArgumentException e) {
                systemLogService.log(null, request.getTarget(), "OTP_RESEND_FAILED", "OTP", null, 
                    String.format("{\"target\":\"%s\",\"reason\":\"Canal invalide\"}", request.getTarget()), "WARNING", false);
                throw new com.example.pariba.exceptions.BadRequestException(
                    "Canal invalide. Valeurs acceptees: EMAIL, SMS, WHATSAPP"
                );
            }
        }
        
        // Regenerer et renvoyer l'OTP
        otpService.generateAndSendOtp(request.getTarget(), channel);
        
        // Log renvoi OTP
        String details = String.format("{\"target\":\"%s\",\"channel\":\"%s\"}", request.getTarget(), channel);
        systemLogService.log(null, request.getTarget(), "OTP_RESENT", "OTP", null, details, "INFO", true);
        
        return ResponseEntity.ok(ApiResponse.success("Code OTP renvoye avec succes", null));
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
        
        // Log demande de réinitialisation
        String details = String.format("{\"phone\":\"%s\"}", request.getPhone());
        systemLogService.log(null, request.getPhone(), "PASSWORD_RESET_REQUESTED", "Person", null, details, "INFO", true);
        
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
        try {
            passwordResetService.resetPassword(request.getTarget(), request.getOtpCode(), request.getNewPassword());
            
            // Log succès
            String details = String.format("{\"target\":\"%s\"}", request.getTarget());
            systemLogService.log(null, request.getTarget(), "PASSWORD_RESET_SUCCESS", "Person", null, details, "INFO", true);
            
            return ResponseEntity.ok(ApiResponse.success("Mot de passe réinitialisé avec succès", null));
        } catch (Exception e) {
            // Log échec
            String details = String.format("{\"target\":\"%s\",\"reason\":\"%s\"}", request.getTarget(), e.getMessage());
            systemLogService.log(null, request.getTarget(), "PASSWORD_RESET_FAILED", "Person", null, details, "WARNING", false);
            throw e;
        }
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
        
        // Logs
        auditService.log(personId, "PASSWORD_CHANGED", "Person", personId, "{}");
        systemLogService.log(personId, "User", "PASSWORD_CHANGED", "Person", personId, "{}", "INFO", true);
        
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
        String personId = currentUser.getPersonId();
        refreshTokenService.revokeToken(request.getRefreshToken());
        
        // Logs
        if (personId != null) {
            auditService.log(personId, "USER_LOGOUT", "Person", personId, "{}");
            systemLogService.log(personId, "User", "USER_LOGOUT", "Person", personId, "{}", "INFO", true);
        }
        
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
