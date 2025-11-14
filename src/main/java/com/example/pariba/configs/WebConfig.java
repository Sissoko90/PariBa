package com.example.pariba.configs;

import com.example.pariba.security.AuditInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration pour servir les fichiers statiques et préfixer les endpoints REST
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    private final AuditInterceptor auditInterceptor;
    
    @Value("${file.upload.dir:./uploads}")
    private String uploadDir;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Enregistrer l'intercepteur pour capturer les IPs
        registry.addInterceptor(auditInterceptor)
                .addPathPatterns("/**");
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir les fichiers uploadés
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
    
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Ajouter le préfixe /api/v1 à tous les controllers REST
        // SAUF les controllers admin (qui commencent par /admin)
        configurer.addPathPrefix("/api/v1", 
            c -> c.getPackageName().contains("com.example.pariba.controllers") 
                 && !c.getSimpleName().contains("Admin")
                 && c.getSimpleName().endsWith("Controller"));
    }
}
