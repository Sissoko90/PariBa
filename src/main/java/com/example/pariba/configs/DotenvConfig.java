package com.example.pariba.configs;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Charge les variables d'environnement depuis le fichier .env
 * AVANT l'initialisation de Spring Boot.
 * 
 * Cette classe est enregistrée dans META-INF/spring.factories
 */
public class DotenvConfig implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMissing()
                    .load();

            Map<String, Object> envMap = new HashMap<>();

            // Charger toutes les variables du .env
            dotenv.entries().forEach(entry -> {
                envMap.put(entry.getKey(), entry.getValue());
            });

            // Ajouter comme source de propriétés avec haute priorité
            environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", envMap));

            System.out.println("✅ Variables d'environnement chargées depuis .env");
            System.out.println("📱 SMS_ENABLED: " + dotenv.get("SMS_ENABLED"));
            System.out.println("📱 SMS_SENDER_ID: " + dotenv.get("SMS_SENDER_ID"));

        } catch (Exception e) {
            System.err.println("⚠️ Impossible de charger le fichier .env: " + e.getMessage());
        }
    }
}
