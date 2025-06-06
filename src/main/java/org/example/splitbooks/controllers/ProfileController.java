package org.example.splitbooks.controllers;


import org.example.splitbooks.dto.request.*;
import org.example.splitbooks.dto.response.ProfileResponse;
import org.example.splitbooks.dto.response.ProfileSetupResponse;
import org.example.splitbooks.services.impl.ProfileServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileServiceImpl profileServiceImpl;

    public ProfileController(ProfileServiceImpl profileServiceImpl) {
        this.profileServiceImpl = profileServiceImpl;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getProfile() {
            ProfileResponse profileResponse = profileServiceImpl.getProfile();
            return ResponseEntity.ok(profileResponse);
    }

    @GetMapping("/{profileId}")
    public ProfileResponse getProfileById(@PathVariable Long profileId) {
        return profileServiceImpl.getProfileById(profileId);
    }


    @PostMapping("/setup")
    public ResponseEntity<ProfileSetupResponse> setup(  @RequestPart("data") ProfileSetupRequest request,
                                                        @RequestPart(value = "avatar", required = false)  MultipartFile avatarFile ) {
        ProfileSetupResponse profileSetupResponse = profileServiceImpl.setUpProfile(request, avatarFile);
        return ResponseEntity.ok(profileSetupResponse);
    }

    @PatchMapping("/edit")
    public ResponseEntity<String> editProfile(@RequestPart(value = "data") EditProfileRequest request,
                                              @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        try {
            profileServiceImpl.editProfile(request, avatar);
            return ResponseEntity.ok("Profile updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating profile: " + e.getMessage());
        }
    }


    @PatchMapping("/edit/genres")
    public ResponseEntity<Void> editGenres(@RequestBody EditGenresRequest request) {
        profileServiceImpl.editGenres(request);
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/edit/preferences")
    public ResponseEntity<Void> editReadingPreferences(@RequestBody EditPreferencesRequest request) {
        profileServiceImpl.editReadingPreferences(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/anonymous")
    public void createAnonymousProfile() {
       profileServiceImpl.createAnonymousProfile();
    }

    @PatchMapping("/toggle")
    public ResponseEntity<String> toggleProfile() {
        try {
            profileServiceImpl.toggleActiveProfileType();
            return ResponseEntity.ok("Profile type switched successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/edit/notifications")
    public ResponseEntity<String> editNotifications(@RequestBody NotificationEnableRequest request) {
        profileServiceImpl.enableNotifications(request);
        return ResponseEntity.ok("Notification toggle successfully.");
    }
}
