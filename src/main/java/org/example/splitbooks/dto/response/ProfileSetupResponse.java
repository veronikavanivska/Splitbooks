package org.example.splitbooks.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProfileSetupResponse {

    private List<Long> selectedGenres;
    private List<Long> preferredFormat;
    private List<Long> preferredLanguages;
    private String phone;
    private String avatarUrl;
    private String firstName;
    private String lastName;
}
