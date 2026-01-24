package com.example.pariba.services.impl;

import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.LoginRequest;
import com.example.pariba.dtos.requests.RegisterRequest;
import com.example.pariba.dtos.responses.AuthResponse;
import com.example.pariba.dtos.responses.PersonResponse;
import com.example.pariba.enums.AppRole;
import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.exceptions.AlreadyExistsException;
import com.example.pariba.exceptions.UnauthorizedException;
import com.example.pariba.models.Person;
import com.example.pariba.models.User;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.repositories.UserRepository;
import com.example.pariba.services.IAuthService;
import com.example.pariba.services.IJwtService;
import com.example.pariba.services.INotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IJwtService jwtService;
    private final INotificationService notificationService;

    public AuthServiceImpl(PersonRepository personRepository, 
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          IJwtService jwtService,
                          INotificationService notificationService) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.notificationService = notificationService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Vérifier si l'email existe déjà
        if (personRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Person", "email", request.getEmail());
        }

        // Vérifier si le téléphone existe déjà
        if (personRepository.existsByPhone(request.getPhone())) {
            throw new AlreadyExistsException("Person", "phone", request.getPhone());
        }

        // Créer la personne
        Person person = new Person();
        person.setPrenom(request.getPrenom());
        person.setNom(request.getNom());
        person.setEmail(request.getEmail());
        person.setPhone(request.getPhone());
        person.setPhoto(request.getPhoto());
        person.setRole(AppRole.USER);
        person = personRepository.save(person);

        // Créer l'utilisateur
        User user = new User();
        user.setPerson(person);
        user.setUsername(request.getEmail()); // Utiliser l'email comme username
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        // Générer le token JWT
        String token = jwtService.generateToken(person.getId(), person.getEmail(), person.getRole());

        // Envoyer notification de bienvenue
        try {
            Map<String, String> variables = new HashMap<>();
            // Les variables prenom et nom seront automatiquement ajoutées par le service
            
            notificationService.sendNotificationWithTemplate(
                person.getId(),
                NotificationType.WELCOME_REGISTRATION,
                NotificationChannel.PUSH,
                variables
            );
            
            // Envoyer aussi par Email
            notificationService.sendNotificationWithTemplate(
                person.getId(),
                NotificationType.WELCOME_REGISTRATION,
                NotificationChannel.EMAIL,
                variables
            );
            
            log.info("✅ Notifications de bienvenue envoyées à {}", person.getEmail());
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi des notifications de bienvenue: {}", e.getMessage());
        }

        return new AuthResponse(token, new PersonResponse(person));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        System.out.println("🔍 LOGIN - Tentative avec username: " + request.getUsername());
        
        // Chercher l'utilisateur par username, email ou phone
        User user = userRepository.findByUsernameOrEmailOrPhone(request.getUsername())
                .orElseThrow(() -> {
                    System.out.println("❌ LOGIN - Utilisateur non trouvé: " + request.getUsername());
                    return new UnauthorizedException(MessageConstants.AUTH_ERROR_INVALID_CREDENTIALS);
                });

        System.out.println("✅ LOGIN - Utilisateur trouvé: " + user.getUsername());
        System.out.println("📧 LOGIN - Email: " + user.getPerson().getEmail());
        System.out.println("📱 LOGIN - Phone: " + user.getPerson().getPhone());
        
        // Vérifier le mot de passe
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            System.out.println("❌ LOGIN - Mot de passe incorrect");
            throw new UnauthorizedException(MessageConstants.AUTH_ERROR_INVALID_CREDENTIALS);
        }
        
        System.out.println("✅ LOGIN - Mot de passe correct");

        Person person = user.getPerson();
        
        // Générer le token JWT
        String token = jwtService.generateToken(person.getId(), person.getEmail(), person.getRole());

        // Vérifier si c'est la première connexion (basé sur createdAt vs updatedAt)
        boolean isFirstLogin = person.getUpdatedAt() == null || 
                              person.getCreatedAt().equals(person.getUpdatedAt());
        
        if (isFirstLogin) {
            try {
                Map<String, String> variables = new HashMap<>();
                
                notificationService.sendNotificationWithTemplate(
                    person.getId(),
                    NotificationType.FIRST_LOGIN,
                    NotificationChannel.PUSH,
                    variables
                );
                
                log.info("✅ Notification première connexion envoyée à {}", person.getEmail());
            } catch (Exception e) {
                log.error("❌ Erreur notification première connexion: {}", e.getMessage());
            }
        }

        return new AuthResponse(token, new PersonResponse(person));
    }
}
