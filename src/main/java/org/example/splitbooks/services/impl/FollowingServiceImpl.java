package org.example.splitbooks.services.impl;

import org.example.splitbooks.dto.response.ShortProfileResponse;
import org.example.splitbooks.entity.Follow;
import org.example.splitbooks.entity.NotificationType;
import org.example.splitbooks.entity.Profile;
import org.example.splitbooks.entity.User;
import org.example.splitbooks.repositories.FollowRepository;
import org.example.splitbooks.repositories.ProfileRepository;
import org.example.splitbooks.repositories.UserRepository;
import org.example.splitbooks.services.FollowingService;
import org.example.splitbooks.services.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowingServiceImpl implements FollowingService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final FollowRepository followRepository;
    private final NotificationServiceImpl notificationService;

    public FollowingServiceImpl(UserRepository userRepository, ProfileRepository profileRepository,
                                FollowRepository followRepository, NotificationServiceImpl notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.profileRepository = profileRepository;
        this.followRepository = followRepository;

    }
    public void follow(Long followingId) {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile follower = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType())
                .orElseThrow(() -> new RuntimeException("Active profile not found"));

        Profile following = profileRepository.findByProfileId(followingId).orElseThrow(()-> new RuntimeException("Profile not found"));

        if(followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new RuntimeException("Already following");
        }

        Follow follow = new Follow();
        follow.setFollowing(following);
        follow.setFollower(follower);
        followRepository.save(follow);

        String message = follower.getUsername() + " started following you";
        notificationService.createAndSend(following, message, NotificationType.FOLLOW);

    }


    public void unfollow(Long followingId) {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile follower = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType())
                .orElseThrow(() -> new RuntimeException("Active profile not found"));

        Profile following = profileRepository.findByProfileId(followingId).orElseThrow(()-> new RuntimeException("Profile not found"));

        Follow follow = followRepository.findByFollowerAndFollowing(follower,following).orElseThrow(() -> new RuntimeException("Not following"));

        followRepository.delete(follow);
    }


    public List<ShortProfileResponse> getFollowers(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return followRepository.findAllByFollowing(profile)
                .stream()
                .map(f -> {
                    Profile follower = f.getFollower();
                    return new ShortProfileResponse(
                            follower.getProfileId(),
                            follower.getUsername(),
                            follower.getAvatarUrl());
                })
                .toList();
    }

    public List<ShortProfileResponse> getFollowing(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return followRepository.findAllByFollower(profile)
                .stream()
                .map(f -> {
                    Profile following = f.getFollowing();
                    return new ShortProfileResponse(
                            following.getProfileId(),
                            following.getUsername(),
                            following.getAvatarUrl());
                })
                .toList();
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
