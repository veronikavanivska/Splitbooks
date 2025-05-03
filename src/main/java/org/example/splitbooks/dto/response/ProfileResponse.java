package org.example.splitbooks.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProfileResponse {
    private String username;
    private String firstName;
    private String lastName;
    private String phone;

    private List<String> genreNames;
    private List<String> formatNames;
    private List<String> languageNames;
}
