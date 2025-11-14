package com.example.pariba.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuration de sécurité Spring Security
 * - Authentification JWT
 * - Autorisation par rôles
 * - CORS configuré
 * - Endpoints publics/protégés
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final RateLimitingFilter rateLimitingFilter;
    private final AdminLoginSuccessHandler adminLoginSuccessHandler;
    private final AdminLoginFailureHandler adminLoginFailureHandler;
    private final XSSFilter xssFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF: Désactivé pour API REST, activé pour dashboard admin
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/v1/**")  // API REST sans CSRF
                // Dashboard admin avec CSRF activé
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Sessions: Stateless pour API, avec session pour admin dashboard
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(3)  // Max 3 sessions par utilisateur (navigateur, mobile, etc.)
                .maxSessionsPreventsLogin(false)  // Invalider l'ancienne session au lieu de bloquer
                .expiredUrl("/admin/login?expired=true")
            )
            .authorizeHttpRequests(auth -> auth
                // Endpoints publics - Health check et Debug
                .requestMatchers("/api/v1/health").permitAll()
                .requestMatchers("/api/v1/debug/**").permitAll()
                
                // Endpoints publics - Authentification (ORDRE IMPORTANT: spécifique avant général)
                .requestMatchers("/api/v1/auth/register").permitAll()
                .requestMatchers("/api/v1/auth/login").permitAll()
                .requestMatchers("/api/v1/auth/otp/send").permitAll()
                .requestMatchers("/api/v1/auth/otp/verify").permitAll()
                .requestMatchers("/api/v1/auth/password/forgot").permitAll()
                .requestMatchers("/api/v1/auth/password/reset").permitAll()
                
                // Endpoints protégés - Authentification (nécessitent un token JWT)
                .requestMatchers("/api/v1/auth/password/change").authenticated()
                .requestMatchers("/api/v1/auth/profile/**").authenticated()
                .requestMatchers("/api/v1/auth/logout").authenticated()
                
                // Endpoints publics - Invitations (lien unique)
                .requestMatchers("/api/v1/invitations/accept/**").permitAll()
                
                // Endpoints publics - Callbacks paiements
                .requestMatchers("/api/v1/payments/orange/callback").permitAll()
                .requestMatchers("/api/v1/payments/moov/callback").permitAll()
                
                // Endpoints publics - Fichiers et monitoring
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                
                // IMPORTANT: Ressources statiques AVANT toute autre règle
                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**", "/favicon.ico").permitAll()
                
                // Swagger/OpenAPI - Réservé aux SUPERADMIN uniquement
                .requestMatchers("/swagger-ui/**").hasRole("SUPERADMIN")
                .requestMatchers("/v3/api-docs/**").hasRole("SUPERADMIN")
                .requestMatchers("/swagger-resources/**").hasRole("SUPERADMIN")
                .requestMatchers("/webjars/**").hasRole("SUPERADMIN")
                
                // Page de login admin - Public (AVANT /admin/**)
                .requestMatchers("/admin/login", "/admin/logout").permitAll()
                
                // Interface Admin Thymeleaf - Réservé aux SUPERADMIN uniquement
                .requestMatchers("/admin/**").hasRole("SUPERADMIN")
                
                // Endpoints protégés - Actuator (monitoring)
                .requestMatchers("/actuator/**").hasRole("SUPERADMIN")
                
                // Tous les autres endpoints nécessitent une authentification
                .anyRequest().authenticated()
            )
            // Configuration login form pour dashboard admin
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .successHandler(adminLoginSuccessHandler)
                .failureHandler(adminLoginFailureHandler)
                .permitAll()
            )
            // Configuration logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/admin/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // Headers de sécurité
            .headers(headers -> headers
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)  // 1 an
                )
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; img-src 'self' data: https:; font-src 'self' https://cdnjs.cloudflare.com;")
                )
                .frameOptions(frame -> frame.deny())
                .xssProtection(xss -> xss.disable())  // Notre filtre XSS personnalisé
            )
            .authenticationProvider(authenticationProvider())
            // Ordre des filtres: Rate Limiting -> XSS -> JWT
            .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(xssFilter, RateLimitingFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, XSSFilter.class);

        return http.build();
    }
    
    /**
     * Configuration CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origines autorisées (à configurer selon l'environnement)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",      // React dev
            "http://localhost:4200",      // Angular dev
            "http://localhost:8081",      // Mobile dev
            "https://pariba.com",         // Production
            "https://www.pariba.com",     // Production
            "https://admin.pariba.com"    // Admin production
        ));
        
        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Headers autorisés
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "X-Requested-With",
            "Cache-Control"
        ));
        
        // Headers exposés
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Disposition"
        ));
        
        // Autoriser les credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Durée de cache de la configuration CORS (1 heure)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    /**
     * PasswordEncoder pour le cryptage des mots de passe
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Force 12 pour plus de sécurité
    }
    
    /**
     * AuthenticationProvider avec UserDetailsService et PasswordEncoder
     * Utilise la nouvelle API non dépréciée
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }
    
    /**
     * AuthenticationManager pour l'authentification
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
