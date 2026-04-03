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
import com.example.pariba.services.IOtpService;
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
    private final IOtpService otpService;

    public AuthServiceImpl(PersonRepository personRepository, 
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          IJwtService jwtService,
                          INotificationService notificationService,
                          IOtpService otpService) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.notificationService = notificationService;
        this.otpService = otpService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Vérifier si l'email existe déjà (seulement si l'email est fourni et non vide)
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (personRepository.existsByEmail(request.getEmail())) {
                throw new AlreadyExistsException("Person", "email", request.getEmail());
            }
        }

        // Vérifier si le téléphone existe déjà
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            if (personRepository.existsByPhone(request.getPhone())) {
                throw new AlreadyExistsException("Person", "phone", request.getPhone());
            }
        }

        // Au moins un identifiant (email ou phone) doit être fourni
        boolean hasEmail = request.getEmail() != null && !request.getEmail().trim().isEmpty();
        boolean hasPhone = request.getPhone() != null && !request.getPhone().trim().isEmpty();
        
        if (!hasEmail && !hasPhone) {
            throw new IllegalArgumentException("Au moins un email ou un numéro de téléphone doit être fourni");
        }

        // Créer la personne
        Person person = new Person();
        person.setPrenom(request.getPrenom());
        person.setNom(request.getNom());
        person.setEmail(hasEmail ? request.getEmail() : null);
        person.setPhone(hasPhone ? request.getPhone() : null);
        person.setPhoto(request.getPhoto());
        person.setRole(AppRole.USER);
        person = personRepository.save(person);

        // Creer l'utilisateur
        User user = new User();
        user.setPerson(person);
        // Utiliser le telephone comme username (prioritaire), sinon l'email
        user.setUsername(hasPhone ? request.getPhone() : request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        // Envoyer notification de bienvenue
        try {
            Map<String, String> variables = new HashMap<>();
            
            notificationService.sendNotificationWithTemplate(
                person.getId(),
                NotificationType.WELCOME_REGISTRATION,
                NotificationChannel.PUSH,
                variables
            );
            
            // Envoyer aussi par Email si disponible
            if (hasEmail) {
                notificationService.sendNotificationWithTemplate(
                    person.getId(),
                    NotificationType.WELCOME_REGISTRATION,
                    NotificationChannel.EMAIL,
                    variables
                );
            }
            
            log.info("Notifications de bienvenue envoyees a {}", hasEmail ? person.getEmail() : person.getPhone());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi des notifications de bienvenue: {}", e.getMessage());
        }

        // Ne pas retourner de token - l'utilisateur doit s'authentifier separement
        // Retourner uniquement les informations de la personne creee
        return new AuthResponse(null, new PersonResponse(person));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("🔍 LOGIN - Tentative avec username: {}", request.getUsername());
        
        // Chercher l'utilisateur par username, email ou phone
        User user = userRepository.findByUsernameOrEmailOrPhone(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("❌ LOGIN - Utilisateur non trouvé: {}", request.getUsername());
                    return new UnauthorizedException(MessageConstants.AUTH_ERROR_INVALID_CREDENTIALS);
                });

        log.info("✅ LOGIN - Utilisateur trouvé: {}", user.getUsername());
        
        // Étape 1: Vérifier l'OTP (obligatoire - validé par @NotBlank dans DTO)
        log.info("🔐 LOGIN - Vérification OTP");
        boolean otpValid = otpService.verifyOtp(request.getUsername(), request.getOtpCode());
        if (!otpValid) {
            log.warn("❌ LOGIN - Code OTP invalide ou expiré");
            throw new UnauthorizedException("Code OTP invalide ou expiré");
        }
        log.info("✅ LOGIN - Code OTP valide");
        
        // Étape 2: Vérifier le mot de passe (obligatoire - validé par @NotBlank dans DTO)
        log.info("🔐 LOGIN - Vérification mot de passe");
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("❌ LOGIN - Mot de passe incorrect");
            throw new UnauthorizedException(MessageConstants.AUTH_ERROR_INVALID_CREDENTIALS);
        }
        log.info("✅ LOGIN - Mot de passe correct");

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
