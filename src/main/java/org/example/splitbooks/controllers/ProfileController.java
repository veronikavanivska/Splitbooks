package org.example.splitbooks.controllers;


import org.example.splitbooks.dto.request.ProfileSetupRequest;
import org.example.splitbooks.dto.response.ProfileResponse;
import org.example.splitbooks.dto.response.ProfileSetupResponse;
import org.example.splitbooks.entity.User;
import org.example.splitbooks.services.ProfileService;
import org.example.splitbooks.services.impl.ProfileServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private ProfileServiceImpl profileServiceImpl;

    public ProfileController(ProfileServiceImpl profileServiceImpl) {
        this.profileServiceImpl = profileServiceImpl;
    }
    @GetMapping("/hey")
    public String hey() {
        return "hey" ;
    }
    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getProfile() {
            ProfileResponse profileResponse = profileServiceImpl.getProfile();
            return ResponseEntity.ok(profileResponse);
    }

    @PostMapping("/setup")
    public ResponseEntity<ProfileSetupResponse> setup(@RequestBody ProfileSetupRequest profileSetupRequest) {
        ProfileSetupResponse profileSetupResponse = profileServiceImpl.setUpProfile(profileSetupRequest);
        return ResponseEntity.ok(profileSetupResponse);
    }

}
