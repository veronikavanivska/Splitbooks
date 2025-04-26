package org.example.splitbooks.dto.response;

import lombok.Data;

@Data
public class LoginResponse {

    private Long userId;
    private String token;
}
