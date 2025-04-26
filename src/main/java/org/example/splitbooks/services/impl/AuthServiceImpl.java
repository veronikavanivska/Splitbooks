package org.example.splitbooks.services.impl;

import org.example.splitbooks.dto.request.LoginRequest;
import org.example.splitbooks.dto.request.RegistrationRequest;
import org.example.splitbooks.dto.response.LoginResponse;
import org.example.splitbooks.entity.Profile;
import org.example.splitbooks.entity.ProfileType;
import org.example.splitbooks.entity.User;
import org.example.splitbooks.repositories.ProfileRepository;
import org.example.splitbooks.repositories.UserRepository;
import org.example.splitbooks.security.JwtUtil;
import org.example.splitbooks.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    public UserRepository userRepository;
    public ProfileRepository profileRepository;
    public JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository,ProfileRepository profileRepository,JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.jwtUtil = jwtUtil;
    }

    public User register(RegistrationRequest registrationRequest) {
        if (profileRepository.existsByUsername(registrationRequest.getUsername()))
            throw new RuntimeException("Username already exists");

        if (userRepository.existsByEmail(registrationRequest.getEmail()))
            throw new RuntimeException("Email already exists");

        User user = new User();

        user.setPassword(registrationRequest.getPassword());
        user.setEmail(registrationRequest.getEmail());
        user.setPhone(registrationRequest.getPhone());
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setActiveProfileType(ProfileType.PUBLIC);
        userRepository.save(user);

        Profile publicProfile = new Profile();

        publicProfile.setUser(user);
        publicProfile.setUsername(registrationRequest.getUsername());

        publicProfile.setType(ProfileType.PUBLIC);

        profileRepository.save(publicProfile);

        return user;

    }

    public LoginResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail());

        if(!user.getPassword().equals(loginRequest.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail()); // <- you need jwtService injected!

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserId(user.getUserId());
        loginResponse.setToken(token);

        return loginResponse;

    }
}
