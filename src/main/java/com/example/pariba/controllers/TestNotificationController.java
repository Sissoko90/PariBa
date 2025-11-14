package com.example.pariba.controllers;

import com.example.pariba.services.IEmailService;
import com.example.pariba.services.IPushNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour tester les notifications (Email et Push)
 * Accessible sans authentification pour faciliter les tests
 */
@RestController
@RequestMapping("/api/test/notifications")
@Slf4j
public class TestNotificationController {
    
    private final IEmailService emailService;
    private final IPushNotificationService pushService;
    
    @Value("${firebase.enabled:false}")
    private boolean firebaseEnabled;
    
    public TestNotificationController(IEmailService emailService, 
                                     IPushNotificationService pushService) {
        this.emailService = emailService;
        this.pushService = pushService;
    }
    
    /**
     * Tester l'envoi d'email
     * GET http://localhost:8081/api/test/notifications/email?to=ton@email.com
     */
    @GetMapping("/email")
    public ResponseEntity<Map<String, Object>> testEmail(
            @RequestParam String to,
            @RequestParam(defaultValue = "Test Pariba") String subject,
            @RequestParam(defaultValue = "Ceci est un email de test depuis Pariba") String message
    ) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            emailService.sendHtmlEmail(to, subject, 
                "<h2>✅ Test Email Pariba</h2>" +
                "<p>" + message + "</p>" +
                "<p><strong>Si vous recevez cet email, le service email fonctionne correctement!</strong></p>"
            );
            
            response.put("success", true);
            response.put("message", "Email envoyé avec succès à " + to);
            response.put("to", to);
            response.put("subject", subject);
            
            log.info("✅ Email de test envoyé à: {}", to);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            log.error("❌ Erreur envoi email de test: {}", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Tester l'envoi de notification push
     * POST http://localhost:8081/api/test/notifications/push
     * Body: { "token": "DEVICE_FCM_TOKEN", "title": "Test", "body": "Message de test" }
     */
    @PostMapping("/push")
    public ResponseEntity<Map<String, Object>> testPush(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        if (!firebaseEnabled) {
            response.put("success", false);
            response.put("error", "Firebase n'est pas activé. Configurez firebase.enabled=true dans application.yml");
            return ResponseEntity.status(400).body(response);
        }
        
        String token = request.get("token");
        String title = request.getOrDefault("title", "Test Pariba");
        String body = request.getOrDefault("body", "Ceci est une notification push de test");
        
        if (token == null || token.isEmpty()) {
            response.put("success", false);
            response.put("error", "Le token FCM est requis");
            return ResponseEntity.status(400).body(response);
        }
        
        try {
            Map<String, String> data = new HashMap<>();
            data.put("test", "true");
            data.put("timestamp", String.valueOf(System.currentTimeMillis()));
            
            pushService.sendToDevice(token, title, body, data);
            
            response.put("success", true);
            response.put("message", "Notification push envoyée avec succès");
            response.put("token", token.substring(0, Math.min(20, token.length())) + "...");
            response.put("title", title);
            response.put("body", body);
            
            log.info("✅ Push de test envoyé au token: {}...", token.substring(0, Math.min(20, token.length())));
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            log.error("❌ Erreur envoi push de test: {}", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Vérifier le statut de Firebase
     * GET http://localhost:8081/api/test/notifications/firebase-status
     */
    @GetMapping("/firebase-status")
    public ResponseEntity<Map<String, Object>> firebaseStatus() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("firebaseEnabled", firebaseEnabled);
        
        if (firebaseEnabled) {
            try {
                // Tenter d'accéder à Firebase pour vérifier la configuration
                response.put("status", "✅ Firebase est activé et configuré");
                response.put("ready", true);
            } catch (Exception e) {
                response.put("status", "⚠️ Firebase activé mais erreur de configuration");
                response.put("ready", false);
                response.put("error", e.getMessage());
            }
        } else {
            response.put("status", "❌ Firebase n'est pas activé");
            response.put("ready", false);
            response.put("help", "Activez firebase.enabled=true dans application.yml après avoir configuré Firebase");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Simuler l'envoi de notification (logs uniquement, sans vraiment envoyer)
     * GET http://localhost:8081/api/test/notifications/simulate
     */
    @GetMapping("/simulate")
    public ResponseEntity<Map<String, Object>> simulateNotification(
            @RequestParam(defaultValue = "PUSH") String type,
            @RequestParam(defaultValue = "Test Title") String title,
            @RequestParam(defaultValue = "Test Message") String message
    ) {
        Map<String, Object> response = new HashMap<>();
        
        log.info("📱 SIMULATION - Type: {}", type);
        log.info("📱 SIMULATION - Titre: {}", title);
        log.info("📱 SIMULATION - Message: {}", message);
        log.info("📱 SIMULATION - Timestamp: {}", System.currentTimeMillis());
        
        response.put("success", true);
        response.put("mode", "simulation");
        response.put("message", "Notification simulée - Vérifiez les logs de l'application");
        response.put("type", type);
        response.put("title", title);
        response.put("body", message);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}
