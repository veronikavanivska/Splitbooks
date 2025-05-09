package org.example.splitbooks.services;

import org.example.splitbooks.dto.request.EditGenresRequest;
import org.example.splitbooks.dto.request.EditPreferencesRequest;
import org.example.splitbooks.dto.request.EditProfileRequest;
import org.example.splitbooks.dto.request.ProfileSetupRequest;
import org.example.splitbooks.dto.response.ProfileResponse;
import org.example.splitbooks.dto.response.ProfileSetupResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface ProfileService {

    public ProfileSetupResponse setUpProfile(ProfileSetupRequest request, MultipartFile avatar);
    public ProfileResponse getProfile();
    public void createAnonymousProfile();
    public void toggleActiveProfileType();
    public void editProfile(EditProfileRequest request, MultipartFile avatar);
    public void editReadingPreferences(EditPreferencesRequest request);
    public void editGenres(EditGenresRequest request);
}
