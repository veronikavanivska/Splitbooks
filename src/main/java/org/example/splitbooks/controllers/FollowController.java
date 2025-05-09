package org.example.splitbooks.controllers;

import org.example.splitbooks.dto.response.ShortProfileResponse;
import org.example.splitbooks.entity.Profile;
import org.example.splitbooks.services.impl.FollowingServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowingServiceImpl followService;

    public FollowController(FollowingServiceImpl followService) {
        this.followService = followService;
    }

    @PostMapping("/{targetProfileId}")
    public ResponseEntity<String> follow(@PathVariable Long targetProfileId) {
        followService.follow(targetProfileId);
        return ResponseEntity.ok("Followed successfully.");
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
