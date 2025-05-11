package org.example.splitbooks.services.impl;

import org.example.splitbooks.dto.request.QuoteRegisterRequest;
import org.example.splitbooks.dto.response.QuoteRegisterResponse;
import org.example.splitbooks.dto.response.QuoteSetResponse;
import org.example.splitbooks.dto.response.ShortProfileResponse;
import org.example.splitbooks.entity.*;
import org.example.splitbooks.repositories.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuoteServiceImpl {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final QuoteSetRepository quoteSetRepository;
    private final FriendSuggestionServiceImpl friendSuggestionService;
    private final QuoteSwipeRepository quoteSwipeRepository;
    private final QuoteMatchRepository quoteMatchRepository;

    public QuoteServiceImpl(UserRepository userRepository, ProfileRepository profileRepository, QuoteMatchRepository quoteMatchRepository, QuoteSwipeRepository quoteSwipeRepository, QuoteSetRepository quoteSetRepository, FriendSuggestionServiceImpl friendSuggestionService) {
        this.userRepository = userRepository;
        this.friendSuggestionService = friendSuggestionService;
        this.quoteSwipeRepository = quoteSwipeRepository;
        this.quoteSetRepository = quoteSetRepository;
        this.profileRepository = profileRepository;
        this.quoteMatchRepository = quoteMatchRepository;
    }

    public QuoteRegisterResponse registerQuote(QuoteRegisterRequest quoteRegisterRequest) {

        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType()).orElseThrow(() -> new RuntimeException("Active profile not found"));

        Optional<QuoteSet> existingQuoteSet = quoteSetRepository.findByProfile(profile);
        if (existingQuoteSet.isPresent()) {
            throw new RuntimeException("Quote set already exists");
        }

        if (quoteRegisterRequest.getQuotes() == null || quoteRegisterRequest.getQuotes().size() != 3) {
            throw new RuntimeException("Quotes size is not 3");
        }

        QuoteSet quoteSet = new QuoteSet();
        quoteSet.setProfile(profile);
        quoteSet.setQuotes(quoteRegisterRequest.getQuotes());

        quoteSetRepository.save(quoteSet);

        profile.setRegisteredInGame(true);
        profileRepository.save(profile);

        QuoteRegisterResponse response = new QuoteRegisterResponse();
        response.setProfileId(profile.getProfileId());
        response.setQuotes(quoteSet.getQuotes());

        return response;
    }

    public List<QuoteSetResponse> getSwipeCards(int batchSize) {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile currentProfile = profileRepository
                .findByUser_UserIdAndType(userId, user.getActiveProfileType())
                .orElseThrow(() -> new RuntimeException("Active profile not found"));

        Long currentProfileId = currentProfile.getProfileId();

        List<Long> alreadySwipedIds = quoteSwipeRepository.findTargetIdsBySwiperId(currentProfileId);

        List<ShortProfileResponse> suggestedFriends = friendSuggestionService.getBestFriendSuggestions().stream()
                .filter(friend -> !alreadySwipedIds.contains(friend.getId()))
                .collect(Collectors.toList());

        List<QuoteSetResponse> swipeCards = new ArrayList<>();
        int currentIndex = 0;
        if(!currentProfile.isRegisteredInGame())
            throw new RuntimeException("Profile is not registered in game");

        while (swipeCards.size() < batchSize && currentIndex < suggestedFriends.size()) {
            ShortProfileResponse friend = suggestedFriends.get(currentIndex);
            Long friendId = friend.getId();

            profileRepository.findByProfileId(friendId).ifPresent(friendProfile -> {
                if (friendProfile.isRegisteredInGame()) {
                    quoteSetRepository.findByProfile_ProfileId(friendId).ifPresent(quoteSet -> {
                        if (!quoteSet.getQuotes().isEmpty()) {
                            QuoteSetResponse response = new QuoteSetResponse();
                            response.setProfileId(friendId);
                            response.setQuotes(quoteSet.getQuotes());
                            swipeCards.add(response);
                        }
                    });
                }
            });

            currentIndex++;
        }

        return swipeCards;
    }
    public void swipe(Long targetProfileId, boolean liked) {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType()).orElseThrow(() -> new RuntimeException("Active profile not found"));
        Profile target = profileRepository.findByProfileId(targetProfileId).orElseThrow(() -> new RuntimeException("Profile not found"));

        if(!profile.isRegisteredInGame())
            throw new RuntimeException("Profile is not registered in game");

        QuoteSwipe swipe = new QuoteSwipe();
        swipe.setSwiper(profile);
        swipe.setTarget(target);
        swipe.setLiked(liked);
        quoteSwipeRepository.save(swipe);

        if (liked) {
            Optional<QuoteSwipe> reverseSwipe = quoteSwipeRepository.findBySwiperProfileIdAndTargetProfileId(targetProfileId, profile.getProfileId());

            if (reverseSwipe.isPresent() && reverseSwipe.get().isLiked()) {

                QuoteMatch match = new QuoteMatch();
                match.setProfile1(profile);
                match.setProfile2(target);
                quoteMatchRepository.save(match);

            }
        }
    }

    public List<QuoteSetResponse> getIncomingSwipes() {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType()).orElseThrow(() -> new RuntimeException("Active profile not found"));

        if(!profile.isRegisteredInGame())
            throw new RuntimeException("Profile is not registered in game");

        List<QuoteSwipe> swipesOnMe = quoteSwipeRepository.findByTargetProfileIdAndLiked(profile.getProfileId(), true);

        Set<Long> alreadySwiped = quoteSwipeRepository.findBySwiperProfileId(profile.getProfileId()).stream()
                .map(swipe -> swipe.getTarget().getProfileId())
                .collect(Collectors.toSet());

        List<QuoteSetResponse> incomingCards = new ArrayList<>();
        for (QuoteSwipe swipe : swipesOnMe) {
            Long swiperId = swipe.getSwiper().getProfileId();

            if (!alreadySwiped.contains(swiperId)) {
                Optional<Profile> profileOpt = profileRepository.findByProfileId(swiperId);
                if (profileOpt.isPresent() && profileOpt.get().isRegisteredInGame()) {
                    Optional<QuoteSet> quoteSetOpt = quoteSetRepository.findByProfile_ProfileId(swiperId);
                    if (quoteSetOpt.isPresent() && !quoteSetOpt.get().getQuotes().isEmpty()) {
                        QuoteSet quoteSet = quoteSetOpt.get();
                        QuoteSetResponse response = new QuoteSetResponse();
                        response.setProfileId(swiperId);
                        response.setQuotes(quoteSet.getQuotes());
                        incomingCards.add(response);
                    }
                }
            }
        }

        return incomingCards;
    }


    private Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getPrincipal().toString());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }
}
