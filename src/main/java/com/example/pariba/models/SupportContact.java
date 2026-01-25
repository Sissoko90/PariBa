package com.example.pariba.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "support_contacts")
public class SupportContact extends BaseEntity {
    
    @NotBlank(message = "L'email est requis")
    @Email(message = "Email invalide")
    @Column(nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Le téléphone est requis")
    @Size(min = 8, max = 20, message = "Le téléphone doit contenir entre 8 et 20 caractères")
    @Column(nullable = false)
    private String phone;
    
    @Column(name = "whatsapp_number")
    private String whatsappNumber;
    
    @Column(name = "support_hours")
    private String supportHours;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    // Constructors
    public SupportContact() {}
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getWhatsappNumber() {
        return whatsappNumber;
    }
    
    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }
    
    public String getSupportHours() {
        return supportHours;
    }
    
    public void setSupportHours(String supportHours) {
        this.supportHours = supportHours;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
}
