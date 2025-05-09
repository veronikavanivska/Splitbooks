package org.example.splitbooks.services.impl;

import org.example.splitbooks.dto.request.EditGenresRequest;
import org.example.splitbooks.dto.request.EditPreferencesRequest;
import org.example.splitbooks.dto.request.EditProfileRequest;
import org.example.splitbooks.dto.request.ProfileSetupRequest;
import org.example.splitbooks.dto.response.ProfileResponse;
import org.example.splitbooks.dto.response.ProfileSetupResponse;
import org.example.splitbooks.entity.*;
import org.example.splitbooks.repositories.*;
import org.example.splitbooks.services.ProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;



//TODO make following and follows also implements the friendship suggestion
//TODO: make it like a quotes with swiping


@Service
public class ProfileServiceImpl implements ProfileService {

    private final CloudinaryServiceImpl cloudinaryService;
    private final ProfileRepository profileRepository;
    private final GenreRepository genreRepository;
    private final LanguageRepository languageRepository;
    private final ReadingFormatRepository readingFormatRepository;
    private final ReadingPreferenceRepository readingPreferenceRepository;
    private final UserRepository userRepository;

    public ProfileServiceImpl(
            ProfileRepository profileRepository,
            UserRepository userRepository,
            GenreRepository genreRepository,
            LanguageRepository languageRepository,
            ReadingFormatRepository readingFormatRepository,
            ReadingPreferenceRepository readingPreferenceRepository,
            CloudinaryServiceImpl cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
        this.profileRepository = profileRepository;
        this.genreRepository = genreRepository;
        this.languageRepository = languageRepository;
        this.readingFormatRepository = readingFormatRepository;
        this.readingPreferenceRepository = readingPreferenceRepository;
        this.userRepository = userRepository;


    }
    public void createAnonymousProfile() {
        Long userId = getAuthenticatedUserId();

        if (profileRepository.findByUser_UserIdAndType(userId, ProfileType.ANONYMOUS).isPresent()) {
            throw new RuntimeException("Anonymous profile already exists.");
        }

        Profile publicProfile = profileRepository.findByUser_UserIdAndType(userId, ProfileType.PUBLIC)
                .orElseThrow(() -> new RuntimeException("Public profile must be created first."));

        Profile anonymousProfile = new Profile();
        anonymousProfile.setType(ProfileType.ANONYMOUS);
        anonymousProfile.setUser(publicProfile.getUser());

        profileRepository.save(anonymousProfile);


    }
    public ProfileSetupResponse setUpProfile(ProfileSetupRequest request, MultipartFile avatar) {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType())
                .orElseThrow(() -> new RuntimeException("Active profile not found"));
        if (profile.isSetupCompleted()) {
            throw new RuntimeException("This profile has already been set up.");
        }

        String avatarUrl = cloudinaryService.uploadAvatar(avatar);

        if (profile.getType() == ProfileType.PUBLIC) {
            setupPublicProfile(profile, request, avatarUrl);
            profile.setSetupCompleted(true);
        } else if (profile.getType() == ProfileType.ANONYMOUS) {
            setupAnonymousProfile(profile, request, avatarUrl);
            profile.setSetupCompleted(true);
        }
        profileRepository.save(profile);

        ProfileSetupResponse response = new ProfileSetupResponse();
        response.setPhone(request.getPhone());
        response.setLastName(request.getLastName());
        response.setFirstName(request.getFirstName());
        response.setPreferredFormat(request.getPreferredFormat());
        response.setPreferredLanguages(request.getPreferredLanguages());
        response.setSelectedGenres(request.getSelectedGenres());
        response.setAvatarUrl(avatarUrl);
        return response;
    }

    private void setupPublicProfile(Profile profile, ProfileSetupRequest request, String avatarUrl ) {

        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhone(request.getPhone());
        profile.setAvatarUrl(avatarUrl);

        List<Genre> selectedGenres = genreRepository.findByGenreIdIn(request.getSelectedGenres());
        profile.setFavoriteGenres(selectedGenres);

        List<ReadingPreference> preferences = createReadingPreferences(request, profile);
        profile.setReadingPreferences(preferences);
    }

    private void setupAnonymousProfile(Profile profile, ProfileSetupRequest request, String avatarUrl ) {
        profile.setUsername(request.getAnonimousUsername());
        profile.setAvatarUrl(avatarUrl);

        List<Genre> selectedGenres = genreRepository.findByGenreIdIn(request.getSelectedGenres());
        profile.setFavoriteGenres(selectedGenres);

        List<ReadingPreference> preferences = createReadingPreferences(request, profile);
        profile.setReadingPreferences(preferences);

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
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType())
                .orElseThrow(() -> new RuntimeException("Active profile not found"));

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

    public void toggleActiveProfileType() {

        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);


        ProfileType currentProfileType = user.getActiveProfileType();

        ProfileType newProfileType;
        if (currentProfileType == ProfileType.PUBLIC) {
            newProfileType = ProfileType.ANONYMOUS;
        } else if (currentProfileType == ProfileType.ANONYMOUS) {
            newProfileType = ProfileType.PUBLIC;
        } else {
            throw new RuntimeException("Invalid profile type");
        }


        user.setActiveProfileType(newProfileType);
        userRepository.save(user);
    }

    public void editProfile(EditProfileRequest request, MultipartFile avatar) {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType())
                .orElseThrow(() -> new RuntimeException("Active profile not found"));

        if (profile.getType() == ProfileType.PUBLIC) {
            if (request.getFirstName() != null && !request.getFirstName().isEmpty())  profile.setFirstName(request.getFirstName());
            if (request.getLastName() != null) profile.setLastName(request.getLastName());
            if (request.getPhone() != null) profile.setPhone(request.getPhone());
        }


        if (request.getUsername() != null && !profile.getUsername().equals(request.getUsername())) {
            if (profileRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username is already taken. Please choose a different one.");
            }

            profile.setUsername(request.getUsername());
        }
        if (avatar != null && !avatar.isEmpty()) {
            cloudinaryService.deleteAvatarByUrl(profile.getAvatarUrl());
            String newAvatar = cloudinaryService.uploadAvatar(avatar);
            profile.setAvatarUrl(newAvatar);
        }
        profileRepository.save(profile);
    }

    public void editGenres(EditGenresRequest request) {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType())
                .orElseThrow(() -> new RuntimeException("Active profile not found"));

        List<Genre> newGenres = genreRepository.findByGenreIdIn(request.getSelectedGenres());


        profile.setFavoriteGenres(newGenres);

        profileRepository.save(profile);
    }

    public void editReadingPreferences(EditPreferencesRequest request) {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType())
                .orElseThrow(() -> new RuntimeException("Active profile not found"));

        List<ReadingPreference> currentPreferences = new ArrayList<>(profile.getReadingPreferences());

        currentPreferences.stream()
                .filter(pref -> !request.getPreferredLanguages().contains(pref.getLanguage().getLanguageId()) ||
                        !request.getPreferredFormats().contains(pref.getFormat().getFormatId()))
                .forEach(pref -> {
                    profile.getReadingPreferences().remove(pref);
                    readingPreferenceRepository.delete(pref);
                });

        for (Long formatId : request.getPreferredFormats()) {
            ReadingFormat format = readingFormatRepository.findById(formatId)
                    .orElseThrow(() -> new RuntimeException("Format not found"));

            for (Long langId : request.getPreferredLanguages()) {
                Language language = languageRepository.findById(langId)
                        .orElseThrow(() -> new RuntimeException("Language not found"));

                boolean alreadyExists = profile.getReadingPreferences().stream()
                        .anyMatch(p -> p.getLanguage().getLanguageId().equals(langId)
                                && p.getFormat().getFormatId().equals(formatId));

                if (!alreadyExists) {
                    ReadingPreference pref = new ReadingPreference();
                    pref.setFormat(format);
                    pref.setLanguage(language);
                    pref.setProfile(profile);

                    profile.getReadingPreferences().add(pref);
                }
            }
        }

        profileRepository.save(profile);
    }


    private Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getPrincipal().toString());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }
}
