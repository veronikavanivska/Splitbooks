package org.example.splitbooks.controllers;

import org.example.splitbooks.dto.response.ShortProfileResponse;
import org.example.splitbooks.entity.Profile;
import org.example.splitbooks.entity.User;
import org.example.splitbooks.services.ProfileService;
import org.example.splitbooks.services.impl.FollowingServiceImpl;
import org.example.splitbooks.services.impl.ProfileServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowingServiceImpl followService;
    private final ProfileServiceImpl profileServiceImpl;
    private final ProfileService profileService;

    public FollowController(FollowingServiceImpl followService, ProfileServiceImpl profileServiceImpl, ProfileService profileService) {
        this.followService = followService;
        this.profileServiceImpl = profileServiceImpl;
        this.profileService = profileService;
    }

    @PostMapping("/{targetProfileId}")
    public ResponseEntity<String> follow(@PathVariable Long targetProfileId) {
        followService.follow(targetProfileId);
        return ResponseEntity.ok("Followed successfully.");
    }

        @GetMapping("/search")
        public ResponseEntity<List<ShortProfileResponse>> search(@RequestParam("username") String username) {
            List<ShortProfileResponse> profiles = followService.searchByUsername(username);
            return ResponseEntity.ok(profiles);
        }

    @GetMapping("/is-following/{targetProfileId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long targetProfileId) {
        return ResponseEntity.ok(followService.isFollowing(targetProfileId));
    }

    @DeleteMapping("/unfollow/{targetProfileId}")
    public ResponseEntity<String> unfollow(@PathVariable Long targetProfileId) {
        followService.unfollow(targetProfileId);
        return ResponseEntity.ok("Unfollowed successfully.");
    }

    @GetMapping("/followers/{profileId}")
        public ResponseEntity<List<ShortProfileResponse>> getFollowers(@PathVariable Long profileId) {
        return ResponseEntity.ok(followService.getFollowers(profileId));
    }

    @GetMapping("/following/{profileId}")
    public ResponseEntity<List<ShortProfileResponse>> getFollowing(@PathVariable Long profileId) {
        return ResponseEntity.ok(followService.getFollowing(profileId));
    }
}
