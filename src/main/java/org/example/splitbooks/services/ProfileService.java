package org.example.splitbooks.services;

import org.example.splitbooks.dto.request.ProfileSetupRequest;
import org.example.splitbooks.dto.response.ProfileResponse;
import org.example.splitbooks.dto.response.ProfileSetupResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface ProfileService {

    public ProfileSetupResponse setUpProfile(ProfileSetupRequest request, MultipartFile avatar);
    public ProfileResponse getProfile();

}
