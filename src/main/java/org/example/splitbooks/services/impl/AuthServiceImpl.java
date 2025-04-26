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
import org.example.splitbooks.security.Regex;
import org.example.splitbooks.services.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final Regex regex;

    public AuthServiceImpl(UserRepository userRepository,ProfileRepository profileRepository,JwtUtil jwtUtil, Regex regex,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.regex = regex;
    }

    public User register(RegistrationRequest registrationRequest) {
        if (profileRepository.existsByUsername(registrationRequest.getUsername()))
            throw new RuntimeException("Username already exists");

        if (userRepository.existsByEmail(registrationRequest.getEmail()))
            throw new RuntimeException("Email already exists");

        if(!regex.isPasswordStrong(registrationRequest.getPassword())){
            throw new RuntimeException("Password must be strong");
        }

        User user = new User();


        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
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

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches( loginRequest.getPassword(),user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserId(user.getUserId());
        loginResponse.setToken(token);

        return loginResponse;

    }
}
