package org.example.splitbooks.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SendMessageResponse {
    private Long messageId;
    private String content;
    private String senderUsername;
    private Long senderId;
    private LocalDateTime timestamp;
}
