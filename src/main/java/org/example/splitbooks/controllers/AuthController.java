package org.example.splitbooks.controllers;

import org.example.splitbooks.dto.request.LoginRequest;
import org.example.splitbooks.dto.request.RegistrationRequest;
import org.example.splitbooks.dto.response.LoginResponse;
import org.example.splitbooks.entity.User;
import org.example.splitbooks.services.impl.AuthServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public AuthServiceImpl authServiceImpl;

    public AuthController(AuthServiceImpl authServiceImpl) {
        this.authServiceImpl = authServiceImpl;
    }
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegistrationRequest registrationRequest) {
        User user = authServiceImpl.register(registrationRequest);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authServiceImpl.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }
}
