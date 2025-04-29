package org.example.splitbooks.dto.response;

import lombok.Data;

@Data
public class RegistrationResponse {
    private long userId;
    private String token;
}
