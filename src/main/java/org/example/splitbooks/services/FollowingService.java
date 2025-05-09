package org.example.splitbooks.services;

import org.example.splitbooks.dto.response.ShortProfileResponse;
import org.example.splitbooks.entity.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FollowingService {

    public void follow(Long followingId);
    public void unfollow(Long followingId);

    public List<ShortProfileResponse> getFollowing(Long profileId);
    public List<ShortProfileResponse> getFollowers(Long profileId);
}
