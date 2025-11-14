package com.example.pariba.controllers;

import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.repositories.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller de debug temporaire
 */
@RestController
@RequestMapping("/debug")
public class DebugController {
    
    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    
    public DebugController(PersonRepository personRepository, UserRepository userRepository) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
    }
    
    @GetMapping("/count")
    public Map<String, Object> getCount() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalPersons", personRepository.count());
        result.put("totalUsers", userRepository.count());
        
        // Liste des persons
        personRepository.findAll().forEach(person -> {
            System.out.println("📋 Person: " + person.getEmail() + " | " + person.getPhone());
        });
        
        // Liste des users
        userRepository.findAll().forEach(user -> {
            System.out.println("🔑 User: " + user.getUsername() + " | PersonId: " + user.getPerson().getId());
        });
        
        return result;
    }
    
    @GetMapping("/check-user")
    public Map<String, Object> checkUser() {
        Map<String, Object> result = new HashMap<>();
        
        // Chercher par email
        var userByEmail = userRepository.findByUsernameOrEmailOrPhone("john.doe@example.com");
        result.put("foundByEmail", userByEmail.isPresent());
        
        // Chercher par phone
        var userByPhone = userRepository.findByUsernameOrEmailOrPhone("+22370123456");
        result.put("foundByPhone", userByPhone.isPresent());
        
        if (userByEmail.isPresent()) {
            result.put("username", userByEmail.get().getUsername());
            result.put("personEmail", userByEmail.get().getPerson().getEmail());
            result.put("personPhone", userByEmail.get().getPerson().getPhone());
        }
        
        return result;
    }
}
