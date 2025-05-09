package org.example.splitbooks.controllers;

import org.example.splitbooks.dto.response.ShortProfileResponse;
import org.example.splitbooks.services.FriendSuggestionService;
import org.example.splitbooks.services.impl.FriendSuggestionServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/suggestions")
public class FriendSuggestionController {
    private final FriendSuggestionServiceImpl friendSuggestionService;

    public FriendSuggestionController(FriendSuggestionServiceImpl friendSuggestionService) {
        this.friendSuggestionService = friendSuggestionService;
    }
    @GetMapping("/best")
    public ResponseEntity<List<ShortProfileResponse>> getBestFriendSuggestions() {

        List<ShortProfileResponse> bestSuggestions = friendSuggestionService.getBestFriendSuggestions();
        return ResponseEntity.ok(bestSuggestions);
    }
}
