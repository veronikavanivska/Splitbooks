package org.example.splitbooks.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProfileResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private int followers;
    private int following;
    private List<String> genreNames;
    private List<String> formatNames;
    private List<String> languageNames;
    private String avatarUrl;
    private boolean hasAnonymous;
    private boolean setupCompleted;
    private boolean anonymous;
}
