package com.example.pariba.dtos.requests;

import com.example.pariba.constants.AppConstants;
import com.example.pariba.constants.ValidationMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    
    @NotBlank(message = ValidationMessages.REQUIRED_PRENOM)
    @Size(min = 2, max = 50, message = ValidationMessages.SIZE_PRENOM)
    private String prenom;
    
    @NotBlank(message = ValidationMessages.REQUIRED_NOM)
    @Size(min = 2, max = 50, message = ValidationMessages.SIZE_NOM)
    private String nom;
    
    @Email(message = ValidationMessages.INVALID_EMAIL)
    private String email; // Email optionnel
    
    @NotBlank(message = ValidationMessages.REQUIRED_PHONE)
    @Pattern(regexp = AppConstants.PHONE_REGEX, message = ValidationMessages.INVALID_PHONE)
    private String phone;
    
    @NotBlank(message = ValidationMessages.REQUIRED_PASSWORD)
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 4 chiffres")
    private String password; // Mot de passe: chiffres uniquement, min 4
    
    private String photo;

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
}
