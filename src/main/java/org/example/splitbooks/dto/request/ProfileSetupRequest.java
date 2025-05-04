package org.example.splitbooks.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ProfileSetupRequest {

    private List<Long> selectedGenres;
    private List<Long> preferredFormat;
    private List<Long> preferredLanguages;
    private String bio;
    private String phone;
    private String avatarUrl;
    private String firstName;
    private String lastName;
    private String anonimousUsername;
}
