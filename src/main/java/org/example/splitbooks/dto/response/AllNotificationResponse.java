package org.example.splitbooks.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllNotificationResponse {
    private String message;
    private LocalDateTime createdAt;
}
