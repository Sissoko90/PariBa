package com.example.pariba.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Configuration Firebase pour les notifications push
 */
@Configuration
@Slf4j
public class FirebaseConfig {
    
    @Value("${firebase.config.file:firebase-adminsdk.json}")
    private String firebaseConfigPath;
    
    @Value("${firebase.enabled:false}")
    private boolean firebaseEnabled;
    
    @PostConstruct
    public void initialize() {
        if (!firebaseEnabled) {
            log.warn("Firebase est désactivé. Les notifications push ne seront pas envoyées.");
            return;
        }
        
        try {
            // Essayer de charger depuis le classpath d'abord
            InputStream serviceAccount;
            try {
                serviceAccount = new ClassPathResource(firebaseConfigPath).getInputStream();
                log.info("Fichier Firebase chargé depuis classpath: {}", firebaseConfigPath);
            } catch (Exception e) {
                // Si pas dans classpath, essayer le chemin absolu
                serviceAccount = new FileInputStream(firebaseConfigPath);
                log.info("Fichier Firebase chargé depuis: {}", firebaseConfigPath);
            }
            
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase initialisé avec succès");
            } else {
                log.info("Firebase déjà initialisé");
            }
            
        } catch (IOException e) {
            log.error("Erreur lors de l'initialisation de Firebase: {}", e.getMessage());
            log.error("Assurez-vous que le fichier '{}' existe et est valide", firebaseConfigPath);
            log.error("Les notifications push ne fonctionneront pas.");
        }
    }
}
