package com.example.pariba.services;

import com.example.pariba.enums.AppRole;

public interface IJwtService {
    String generateToken(String personId, String email, AppRole role);
    String getPersonIdFromToken(String token);
    boolean validateToken(String token);
}
