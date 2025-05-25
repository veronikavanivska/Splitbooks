package org.example.splitbooks.dto.request;

import lombok.Data;

@Data
public class SendMessageRequest {
    private Long chatId;
    private String content;
}
