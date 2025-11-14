package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {
    
    @Size(min = 2, max = 50, message = ValidationMessages.SIZE_PRENOM)
    private String prenom;
    
    @Size(min = 2, max = 50, message = ValidationMessages.SIZE_NOM)
    private String nom;
    
    @Email(message = ValidationMessages.INVALID_EMAIL)
    private String email;
    
    private String photo;

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
}
