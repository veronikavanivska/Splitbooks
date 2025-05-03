package org.example.splitbooks.services.impl;

import org.example.splitbooks.dto.request.ProfileSetupRequest;
import org.example.splitbooks.dto.response.ProfileResponse;
import org.example.splitbooks.dto.response.ProfileSetupResponse;
import org.example.splitbooks.entity.*;
import org.example.splitbooks.repositories.*;
import org.example.splitbooks.services.ProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final GenreRepository genreRepository;
    private final LanguageRepository languageRepository;
    private final ReadingFormatRepository readingFormatRepository;
    private final ReadingPreferenceRepository readingPreferenceRepository;

    public ProfileServiceImpl(
            ProfileRepository profileRepository,
            GenreRepository genreRepository,
            LanguageRepository languageRepository,
            ReadingFormatRepository readingFormatRepository,
            ReadingPreferenceRepository readingPreferenceRepository) {
        this.profileRepository = profileRepository;
        this.genreRepository = genreRepository;
        this.languageRepository = languageRepository;
        this.readingFormatRepository = readingFormatRepository;
        this.readingPreferenceRepository = readingPreferenceRepository;
    }

    public ProfileSetupResponse setUpProfile(ProfileSetupRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getPrincipal());
        Long userId = Long.parseLong(authentication.getPrincipal().toString());

        Profile profile = profileRepository.findByUser_UserId(userId);

        if (profile.getType() == ProfileType.PUBLIC) {
            setupPublicProfile(profile, request);
        } else if (profile.getType() == ProfileType.ANONYMOUS) {
            setupAnonymousProfile(profile, request);
        }
        profileRepository.save(profile);

        ProfileSetupResponse response = new ProfileSetupResponse();
        response.setPhone(request.getPhone());
        response.setLastName(request.getLastName());
        response.setFirstName(request.getFirstName());
        response.setAvatarUrl(request.getAvatarUrl());
        response.setPreferredFormat(request.getPreferredFormat());
        response.setPreferredLanguages(request.getPreferredLanguages());
        response.setSelectedGenres(request.getSelectedGenres());

        return response;
    }

    private void setupPublicProfile(Profile profile, ProfileSetupRequest request) {
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhone(request.getPhone());
        profile.setAvatarUrl(request.getAvatarUrl());

        List<Genre> selectedGenres = genreRepository.findByGenreIdIn(request.getSelectedGenres());
        profile.setFavoriteGenres(selectedGenres);

        List<ReadingPreference> preferences = createReadingPreferences(request, profile);
        profile.setReadingPreferences(preferences);
    }

    private void setupAnonymousProfile(Profile profile, ProfileSetupRequest request) {

    }
    private List<ReadingPreference> createReadingPreferences(ProfileSetupRequest request, Profile profile) {
        List<ReadingPreference> preferences = new ArrayList<>();

        for (Long formatId : request.getPreferredFormat()) {
            ReadingFormat format = readingFormatRepository.findById(formatId)
                    .orElseThrow(() -> new RuntimeException("Format not found"));

            for (Long langId : request.getPreferredLanguages()) {
                Language language = languageRepository.findById(langId)
                        .orElseThrow(() -> new RuntimeException("Language not found"));

                ReadingPreference pref = new ReadingPreference();
                pref.setFormat(format);
                pref.setLanguage(language);
                pref.setProfile(profile);
                preferences.add(pref);
            }
        }

        return preferences;
    }

    public ProfileResponse getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getPrincipal().toString());

        Profile profile = profileRepository.findByUser_UserId(userId);
        if (profile == null) throw new RuntimeException("Profile not found");

        ProfileResponse response = new ProfileResponse();
        response.setUsername(profile.getUsername());

        if(profile.getType() == ProfileType.PUBLIC) {
            response.setFirstName(profile.getFirstName());
            response.setLastName(profile.getLastName());
            response.setPhone(profile.getPhone());
        }

        response.setGenreNames(profile.getFavoriteGenres().stream().map(Genre::getGenreName).distinct().toList());
        response.setFormatNames(profile.getReadingPreferences().stream().map(p -> p.getFormat().getFormatName()).distinct().toList());
        response.setLanguageNames(profile.getReadingPreferences().stream().map(p -> p.getLanguage().getLanguageName()).distinct().toList());

        return response;
    }
}
