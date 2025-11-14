package com.example.pariba.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Swagger/OpenAPI pour la documentation de l'API
 * Accessible via: http://localhost:8080/swagger-ui.html
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Pariba API",
        version = "1.0.0",
        description = """
            API REST pour la plateforme Pariba - Gestion de tontines (groupes d'épargne rotatifs)
            
            ## Fonctionnalités principales:
            - 🔐 Authentification JWT avec OTP
            - 👥 Gestion des groupes de tontine
            - 💰 Suivi des contributions et paiements
            - 🔄 Gestion des tours de rotation
            - 📊 Exports et statistiques
            - 🔔 Notifications multi-canal
            - 💎 Système d'abonnements
            
            ## Authentification:
            1. Créer un compte: POST /api/auth/register
            2. Se connecter: POST /api/auth/login
            3. Utiliser le token JWT dans le header: Authorization: Bearer {token}
            
            ## Codes de statut:
            - 200: Succès
            - 201: Créé
            - 400: Requête invalide
            - 401: Non authentifié
            - 403: Non autorisé
            - 404: Non trouvé
            - 500: Erreur serveur
            """,
        contact = @Contact(
            name = "Équipe Pariba",
            email = "support@pariba.com",
            url = "https://pariba.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            description = "Serveur de développement",
            url = "http://localhost:8080"
        ),
        @Server(
            description = "Serveur de production",
            url = "https://api.pariba.com"
        )
    },
    security = {
        @SecurityRequirement(name = "bearerAuth")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "Authentification JWT. Format: Bearer {token}",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // Configuration via annotations
}
