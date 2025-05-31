package org.example.splitbooks.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShortChatResponse {
    Long chatId;
    String chatName;
}
