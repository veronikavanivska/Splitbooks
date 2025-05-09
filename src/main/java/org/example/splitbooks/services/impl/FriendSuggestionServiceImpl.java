package org.example.splitbooks.services.impl;

import org.example.splitbooks.dto.response.ShortProfileResponse;

import org.example.splitbooks.entity.Genre;
import org.example.splitbooks.entity.Profile;
import org.example.splitbooks.entity.User;
import org.example.splitbooks.repositories.BookRepository;
import org.example.splitbooks.repositories.GenreRepository;
import org.example.splitbooks.repositories.ProfileRepository;
import org.example.splitbooks.repositories.UserRepository;
import org.example.splitbooks.services.FriendSuggestionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FriendSuggestionServiceImpl implements FriendSuggestionService {

    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FollowingServiceImpl followingService;

   public FriendSuggestionServiceImpl(FollowingServiceImpl followingService,ProfileRepository profileRepository, BookRepository bookRepository, GenreRepository genreRepository, UserRepository userRepository) {
       this.profileRepository = profileRepository;
       this.bookRepository = bookRepository;
       this.genreRepository = genreRepository;
       this.userRepository = userRepository;
       this.followingService = followingService;
   }

    public List<ShortProfileResponse> getBestFriendSuggestions() {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType()).orElseThrow(()->new RuntimeException("not found"));
        List<ShortProfileResponse> suggestedFriends = getFriendSuggestions();

        return filterOutFriendsAndFollowers(suggestedFriends);

    }
   public List<ShortProfileResponse> getFriendSuggestions() {
       Long userId = getAuthenticatedUserId();
       User user = getUserById(userId);

       Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType()).orElseThrow(()-> new RuntimeException("Active profile not found"));

       Set<Long> userGenreIds = profile.getFavoriteGenres().stream()
               .map(Genre::getGenreId)
               .collect(Collectors.toSet());
       Set<String> userBookIds = profile.getBookProfiles().stream()
               .map(bp -> bp.getBook().getVolumeId())
               .collect(Collectors.toSet());

       List<Profile> allUsers = profileRepository.findAll();

       Map<Profile,Integer> similarityScore = new HashMap<>();


       for (Profile potentialFriend : allUsers) {
           if (!potentialFriend.getProfileId().equals(profile.getProfileId())) {
               int score = calculateCommonInterestScore(potentialFriend, userGenreIds, userBookIds);
               if (score > 0) {
                   similarityScore.put(potentialFriend, score);
               }
           }
       }

       return similarityScore.entrySet().stream()
               .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
               .map(entry -> mapToShortProfileResponse(entry.getKey()))
               .collect(Collectors.toList());
   }

    public List<ShortProfileResponse> filterOutFriendsAndFollowers( List<ShortProfileResponse> suggestedFriends) {
        // Get the user's following list
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType()).orElseThrow(()-> new RuntimeException("Active profile not found"));

        List<ShortProfileResponse> following = followingService.getFollowing(profile.getProfileId());
        List<ShortProfileResponse> followers = followingService.getFollowers(profile.getProfileId());

        Set<Long> alreadyConnectedProfileIds = new HashSet<>();
        for (ShortProfileResponse p : following) {
            alreadyConnectedProfileIds.add(p.getId());
        }
        for (ShortProfileResponse p : followers) {
            alreadyConnectedProfileIds.add(p.getId());
        }

        return suggestedFriends.stream()
                .filter(suggestedProfile -> !alreadyConnectedProfileIds.contains(suggestedProfile.getId()))
                .collect(Collectors.toList());
    }



    private int calculateCommonInterestScore(Profile potentialFriend, Set<Long> currentUserGenres, Set<String> currentUserBooks) {
        int score = 0;


        Set<Long> friendGenreIds = potentialFriend.getFavoriteGenres().stream()
                .map(Genre::getGenreId)
                .collect(Collectors.toSet());

        Set<String> friendBookIds = potentialFriend.getBookProfiles().stream()
                .map(bp -> bp.getBook().getVolumeId())
                .collect(Collectors.toSet());

        for (Long genreId : currentUserGenres) {
            if (friendGenreIds.contains(genreId)) {
                score += 1;
            }
        }
        for(String bookId : currentUserBooks) {
            if(friendBookIds.contains(bookId)) {
                score += 2;
            }
        }

        return score;
    }


    private ShortProfileResponse mapToShortProfileResponse(Profile profile) {
        ShortProfileResponse response = new ShortProfileResponse();
        response.setId(profile.getProfileId());
        response.setUsername(profile.getUsername());
        response.setAvatarUrl(profile.getAvatarUrl());
        return response;
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
