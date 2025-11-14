package com.example.pariba.services;

import com.example.pariba.dtos.requests.LoginRequest;
import com.example.pariba.dtos.requests.RegisterRequest;
import com.example.pariba.dtos.responses.AuthResponse;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
