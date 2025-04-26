package org.example.splitbooks.services;

import org.example.splitbooks.dto.request.LoginRequest;
import org.example.splitbooks.dto.request.RegistrationRequest;
import org.example.splitbooks.dto.response.LoginResponse;
import org.example.splitbooks.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public interface AuthService {

    public User register(RegistrationRequest registrationRequest);
    public LoginResponse login(LoginRequest loginRequest);
}
