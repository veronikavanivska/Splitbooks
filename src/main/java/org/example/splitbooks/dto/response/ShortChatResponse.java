package org.example.splitbooks.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ShortChatResponse {
    Long chatId;
    String chatName;
    String chatProfileUrl;
    boolean isGroupChat;
    LocalDateTime lastUpdated;
}
