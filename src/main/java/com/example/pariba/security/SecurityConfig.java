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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/v1/**")
                .ignoringRequestMatchers("/admin/subscription-plans/**")
                .ignoringRequestMatchers("/admin/api/**")
                .ignoringRequestMatchers("/admin/advertisements/upload-image")
                .ignoringRequestMatchers("/admin/subscriptions", "/admin/subscription-stats", "/admin/subscription-plans-view")
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(3)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/admin/login?expired=true")
            )
            .authorizeHttpRequests(auth -> auth

                // ---------- Swagger / OpenAPI ----------
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()

                // ---------- Endpoints publics ----------
                .requestMatchers("/api/v1/health").permitAll()
                .requestMatchers("/api/v1/debug/**").permitAll()

                .requestMatchers("/api/v1/auth/register").permitAll()
                .requestMatchers("/api/v1/auth/login").permitAll()
                .requestMatchers("/api/v1/auth/otp/send").permitAll()
                .requestMatchers("/api/v1/auth/otp/verify").permitAll()
                .requestMatchers("/api/v1/auth/password/forgot").permitAll()
                .requestMatchers("/api/v1/auth/password/reset").permitAll()

                .requestMatchers("/api/v1/invitations/accept/**").permitAll()
                .requestMatchers("/api/v1/payments/orange/callback").permitAll()
                .requestMatchers("/api/v1/payments/moov/callback").permitAll()

                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()

                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**", "/favicon.ico").permitAll()

                // ---------- Admin login ----------
                .requestMatchers("/admin/login", "/admin/perform-login", "/admin/logout").permitAll()

                // ---------- Admin sécurisé ----------
                .requestMatchers("/admin/**").hasRole("SUPERADMIN")
                .requestMatchers("/actuator/**").hasRole("SUPERADMIN")

                // ---------- Auth obligatoire ----------
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/perform-login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(adminLoginSuccessHandler)
                .failureHandler(adminLoginFailureHandler)
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/admin/login?error=access_denied")
                .authenticationEntryPoint((request, response, authException) -> {
                    if (request.getRequestURI().startsWith("/admin/")) {
                        response.sendRedirect("/admin/login?error=unauthorized");
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    }
                })
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/admin/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .headers(headers -> headers
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
                )
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; img-src 'self' data: https:; font-src 'self' https://cdnjs.cloudflare.com;")
                )
                .frameOptions(frame -> frame.deny())
                .xssProtection(xss -> xss.disable())
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(xssFilter, RateLimitingFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, XSSFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:4200",
            "http://localhost:8081",
            "https://pariba.com",
            "https://www.pariba.com",
            "https://admin.pariba.com"
        ));
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "X-Requested-With",
            "Cache-Control"
        ));
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Disposition"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
