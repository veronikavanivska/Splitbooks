package org.example.splitbooks.dto.request;

import lombok.Data;

@Data
public class EditProfileRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String username;
}
