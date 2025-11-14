package com.example.pariba.security;

import com.example.pariba.models.Person;
import com.example.pariba.models.User;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Service personnalisé pour charger les détails de l'utilisateur
 * Utilisé par Spring Security pour l'authentification
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("🔍 Tentative de connexion avec username: {}", username);
        
        // Chercher par username, email OU téléphone
        User user = userRepository.findByUsernameOrEmailOrPhone(username)
                .orElseThrow(() -> {
                    log.error("❌ Utilisateur non trouvé avec: {}", username);
                    return new UsernameNotFoundException("Utilisateur non trouvé: " + username);
                });
        
        log.info("✅ Utilisateur trouvé: {}", username);
        
        // Récupérer la personne associée
        Person person = user.getPerson();
        if (person == null) {
            log.error("❌ Profil utilisateur incomplet pour: {}", username);
            throw new UsernameNotFoundException("Profil utilisateur incomplet");
        }
        
        log.info("✅ Personne associée trouvée - ID: {}, Rôle: {}", person.getId(), person.getRole());
        
        // Construire les autorités (rôles)
        Collection<GrantedAuthority> authorities = getAuthorities(person);
        
        log.info("✅ Autorités: {}", authorities);
        
        // Retourner UserDetails avec l'ID de la personne comme username
        return org.springframework.security.core.userdetails.User.builder()
                .username(person.getId()) // Utiliser l'ID de la personne comme identifiant
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
    
    /**
     * Récupère les autorités (rôles) d'une personne
     */
    private Collection<GrantedAuthority> getAuthorities(Person person) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Ajouter le rôle global de la personne
        if (person.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + person.getRole().name()));
        }
        
        // Par défaut, tous les utilisateurs ont le rôle USER
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        return authorities;
    }
    
    /**
     * Charge un utilisateur par son ID de personne
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserByPersonId(String personId) throws UsernameNotFoundException {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new UsernameNotFoundException("Personne non trouvée: " + personId));
        
        User user = userRepository.findByPersonId(personId)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        
        Collection<GrantedAuthority> authorities = getAuthorities(person);
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(person.getId())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
