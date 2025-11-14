package com.example.pariba.dtos.responses;

import com.example.pariba.enums.AppRole;
import com.example.pariba.models.Person;

import java.time.Instant;

public class PersonResponse {
    
    private String id;
    private String prenom;
    private String nom;
    private String email;
    private String phone;
    private String photo;
    private AppRole role;
    private Instant createdAt;

    public PersonResponse() {}

    public PersonResponse(Person person) {
        this.id = person.getId();
        this.prenom = person.getPrenom();
        this.nom = person.getNom();
        this.email = person.getEmail();
        this.phone = person.getPhone();
        this.photo = person.getPhoto();
        this.role = person.getRole();
        this.createdAt = person.getCreatedAt();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public AppRole getRole() { return role; }
    public void setRole(AppRole role) { this.role = role; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
