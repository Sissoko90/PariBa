package com.example.pariba.configs;

import com.example.pariba.enums.AppRole;
import com.example.pariba.models.Person;
import com.example.pariba.models.User;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;



/**
 * Seeder pour créer le compte SUPERADMIN par défaut
 * Identifiants:
 * - Téléphone: +22370000000
 * - Email: admin@pariba.com
 * - Mot de passe: Admin123!
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"default","dev","prod"}) // ← inclure prod (ou supprime @Profile)
@Order(1)                          // s’exécute tôt
@Transactional 
public class DataSeeder implements CommandLineRunner {
    
    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        seedSuperAdmin();
    }
    
    private void seedSuperAdmin() {
        String superAdminPhone = "+22370000000";
        String superAdminEmail = "admin@pariba.com";
        
        // Vérifier si le SUPERADMIN existe déjà
        if (userRepository.findByUsername(superAdminPhone).isPresent()) {
            log.info("SUPERADMIN existe déjà - Seeding ignoré");
            return;
        }
        
        try {
            // Créer la personne SUPERADMIN
            Person superAdmin = new Person();
            superAdmin.setNom("Admin");
            superAdmin.setPrenom("Super");
            superAdmin.setPhone(superAdminPhone);
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setRole(AppRole.SUPERADMIN);
            superAdmin.setCreatedAt(java.time.Instant.now());
            superAdmin.setUpdatedAt(java.time.Instant.now());
            
            Person savedPerson = personRepository.save(superAdmin);
            log.info("Personne SUPERADMIN créée avec ID: {}", savedPerson.getId());
            
            // Créer l'utilisateur SUPERADMIN
            User user = new User();
            user.setUsername(superAdminPhone);
            user.setPassword(passwordEncoder.encode("Admin123!"));
            user.setPerson(savedPerson);
            user.setCreatedAt(java.time.Instant.now());
            user.setUpdatedAt(java.time.Instant.now());
            
            userRepository.save(user);
            log.info("Utilisateur SUPERADMIN créé");
            
            log.info("========================================");
            log.info("SUPERADMIN créé avec succès !");
            log.info("========================================");
            log.info("Email: {}", superAdminEmail);
            log.info("Téléphone: {}", superAdminPhone);
            log.info("Mot de passe: Admin123!");
            log.info("Rôle: SUPERADMIN");
            log.info("========================================");
            log.info("Dashboard Admin: http://localhost:8080/admin/dashboard");
            log.info("Swagger UI: http://localhost:8080/swagger-ui.html");
            log.info("========================================");
            
        } catch (Exception e) {
            log.error("Erreur lors de la création du SUPERADMIN", e);
        }
    }
}
