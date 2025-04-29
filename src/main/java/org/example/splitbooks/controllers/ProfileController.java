package org.example.splitbooks.controllers;


import org.example.splitbooks.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @GetMapping("/hey")
    public String hey() {
        return "hey" ;
    }
    @GetMapping("/me")
    public ResponseEntity<Long> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getPrincipal());
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        return ResponseEntity.ok(userId);
    }
}
