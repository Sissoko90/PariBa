package com.example.pariba.models;

import com.example.pariba.enums.AppRole;
import jakarta.persistence.*;

@Entity
@Table(name = "persons", indexes = {
  @Index(columnList = "phone", unique = true),
  @Index(columnList = "email", unique = true)
})
public class Person extends BaseEntity {
    
    @Column(nullable = false) 
    private String prenom;
    
    @Column(nullable = false) 
    private String nom;
    
    @Column(unique = true) 
    private String email;
    
    @Column(unique = true) 
    private String phone;
    
    private String photo; // url/base64
    
    @Column(name = "fcm_token", length = 500)
    private String fcmToken;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppRole role = AppRole.USER;
    
    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private User user;

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
    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
    public AppRole getRole() { return role; }
    public void setRole(AppRole role) { this.role = role; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
