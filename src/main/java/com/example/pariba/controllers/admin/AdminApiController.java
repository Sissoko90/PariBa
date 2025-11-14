package com.example.pariba.controllers.admin;

import com.example.pariba.models.Person;
import com.example.pariba.models.TontineGroup;
import com.example.pariba.models.Payment;
import com.example.pariba.repositories.*;
import com.example.pariba.services.impl.FileGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API REST pour les actions admin (AJAX)
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('SUPERADMIN')")
public class AdminApiController {
    
    private final PersonRepository personRepository;
    private final TontineGroupRepository tontineGroupRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final FileGeneratorService fileGeneratorService;
    
    /**
     * Recherche d'utilisateurs
     */
    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("🔍 Recherche utilisateurs: {}", query);
        
        try {
            var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            
            // Recherche par nom, prénom, email ou téléphone
            Page<Person> results = personRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContaining(
                query, query, query, query, pageable
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("users", results.getContent());
            response.put("totalPages", results.getTotalPages());
            response.put("totalElements", results.getTotalElements());
            response.put("currentPage", page);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur recherche utilisateurs", e);
            return ResponseEntity.internalServerError().body("Erreur lors de la recherche");
        }
    }
    
    /**
     * Détails d'un utilisateur
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserDetails(@PathVariable String id) {
        log.info("📋 Détails utilisateur: {}", id);
        
        return personRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Recherche de groupes
     */
    @GetMapping("/groups/search")
    public ResponseEntity<?> searchGroups(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("🔍 Recherche groupes: {}", query);
        
        try {
            var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            
            // Recherche par nom ou description
            Page<TontineGroup> results = tontineGroupRepository.findByNomContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                query, query, pageable
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("groups", results.getContent());
            response.put("totalPages", results.getTotalPages());
            response.put("totalElements", results.getTotalElements());
            response.put("currentPage", page);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur recherche groupes", e);
            return ResponseEntity.internalServerError().body("Erreur lors de la recherche");
        }
    }
    
    /**
     * Détails d'un groupe
     */
    @GetMapping("/groups/{id}")
    public ResponseEntity<?> getGroupDetails(@PathVariable String id) {
        log.info("📋 Détails groupe: {}", id);
        
        return tontineGroupRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Recherche de paiements
     */
    @GetMapping("/payments/search")
    public ResponseEntity<?> searchPayments(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("🔍 Recherche paiements: {}", query);
        
        try {
            // Pour l'instant, retourner tous les paiements avec pagination
            var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Payment> results = paymentRepository.findAll(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("payments", results.getContent());
            response.put("totalPages", results.getTotalPages());
            response.put("totalElements", results.getTotalElements());
            response.put("currentPage", page);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur recherche paiements", e);
            return ResponseEntity.internalServerError().body("Erreur lors de la recherche");
        }
    }
    
    /**
     * Détails d'un paiement
     */
    @GetMapping("/payments/{id}")
    public ResponseEntity<?> getPaymentDetails(@PathVariable String id) {
        log.info("📋 Détails paiement: {}", id);
        
        return paymentRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Supprimer un utilisateur (soft delete)
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        log.info("🗑️ Suppression utilisateur: {}", id);
        
        return personRepository.findById(id)
            .map(person -> {
                personRepository.delete(person);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Utilisateur supprimé avec succès");
                
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Supprimer un groupe (soft delete)
     */
    @DeleteMapping("/groups/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable String id) {
        log.info("🗑️ Suppression groupe: {}", id);
        
        return tontineGroupRepository.findById(id)
            .map(group -> {
                tontineGroupRepository.delete(group);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Groupe supprimé avec succès");
                
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Statistiques globales
     */
    @GetMapping("/stats/global")
    public ResponseEntity<?> getGlobalStats() {
        log.info("📊 Récupération statistiques globales");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", personRepository.count());
        stats.put("totalGroups", tontineGroupRepository.count());
        stats.put("totalPayments", paymentRepository.count());
        stats.put("activeUsers", userRepository.count()); // Tous les users
        stats.put("activeGroups", tontineGroupRepository.count()); // Tous les groupes
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Export utilisateurs en PDF
     */
    @GetMapping("/export/users/pdf")
    public ResponseEntity<Resource> exportUsersPdf() {
        log.info("📄 Export utilisateurs en PDF");
        
        try {
            List<Person> users = personRepository.findAll();
            String fileName = fileGeneratorService.generateUsersPdf(users, "admin");
            
            Resource resource = new FileSystemResource(Paths.get("./exports/" + fileName));
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (Exception e) {
            log.error("Erreur export PDF", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Export utilisateurs en Excel
     */
    @GetMapping("/export/users/excel")
    public ResponseEntity<Resource> exportUsersExcel() {
        log.info("📊 Export utilisateurs en Excel");
        
        try {
            List<Person> users = personRepository.findAll();
            String fileName = fileGeneratorService.generateUsersExcel(users, "admin");
            
            Resource resource = new FileSystemResource(Paths.get("./exports/" + fileName));
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            log.error("Erreur export Excel", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Export groupes en PDF
     */
    @GetMapping("/export/groups/pdf")
    public ResponseEntity<Resource> exportGroupsPdf() {
        log.info("📄 Export groupes en PDF");
        
        try {
            List<TontineGroup> groups = tontineGroupRepository.findAll();
            String fileName = fileGeneratorService.generateGroupsPdf(groups, "admin");
            
            Resource resource = new FileSystemResource(Paths.get("./exports/" + fileName));
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (Exception e) {
            log.error("Erreur export PDF", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Export groupes en Excel
     */
    @GetMapping("/export/groups/excel")
    public ResponseEntity<Resource> exportGroupsExcel() {
        log.info("📊 Export groupes en Excel");
        
        try {
            List<TontineGroup> groups = tontineGroupRepository.findAll();
            String fileName = fileGeneratorService.generateGroupsExcel(groups, "admin");
            
            Resource resource = new FileSystemResource(Paths.get("./exports/" + fileName));
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            log.error("Erreur export Excel", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Export paiements en PDF
     */
    @GetMapping("/export/payments/pdf")
    public ResponseEntity<Resource> exportPaymentsPdf() {
        log.info("📄 Export paiements en PDF");
        
        try {
            List<Payment> payments = paymentRepository.findAll();
            String fileName = fileGeneratorService.generatePaymentsPdf(payments, "admin");
            
            Resource resource = new FileSystemResource(Paths.get("./exports/" + fileName));
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (Exception e) {
            log.error("Erreur export PDF", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Export paiements en Excel
     */
    @GetMapping("/export/payments/excel")
    public ResponseEntity<Resource> exportPaymentsExcel() {
        log.info("📊 Export paiements en Excel");
        
        try {
            List<Payment> payments = paymentRepository.findAll();
            String fileName = fileGeneratorService.generatePaymentsExcel(payments, "admin");
            
            Resource resource = new FileSystemResource(Paths.get("./exports/" + fileName));
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            log.error("Erreur export Excel", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
