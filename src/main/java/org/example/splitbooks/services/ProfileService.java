package org.example.splitbooks.services;

import org.example.splitbooks.dto.request.*;
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
    public void enableNotifications(NotificationEnableRequest request);
}
