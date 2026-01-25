package com.example.pariba.dtos.responses;

public class SupportContactResponse {
    
    private String id;
    private String email;
    private String phone;
    private String whatsappNumber;
    private String supportHours;
    private Boolean active;
    
    public SupportContactResponse() {}
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
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
