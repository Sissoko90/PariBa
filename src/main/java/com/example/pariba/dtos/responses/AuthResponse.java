package com.example.pariba.dtos.responses;

public class AuthResponse {
    
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private PersonResponse person;

    public AuthResponse(String token, PersonResponse person) {
        this.token = token;
        this.person = person;
    }

    public AuthResponse(String token, String refreshToken, PersonResponse person) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.person = person;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public PersonResponse getPerson() { return person; }
    public void setPerson(PersonResponse person) { this.person = person; }
}
