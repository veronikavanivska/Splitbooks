package org.example.splitbooks.dto.request;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
public class RegistrationRequest {

    private String email;
    private String password;
//    private String phone;
//    private String firstName;
//    private String lastName;
    private String username;
}
