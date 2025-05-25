package org.example.splitbooks.dto.response;

import lombok.Data;
import org.example.splitbooks.entity.ChatType;
import org.example.splitbooks.entity.GroupChatType;
import org.example.splitbooks.entity.ProfileType;

import java.util.List;

@Data
public class ChatResponse {
    private Long chatId;
    private String groupName;
    private boolean isGroup;
    private ChatType chatType;
    private GroupChatType groupChatType;

    private List<ParticipantInfo> participants;

    @Data
    public static class ParticipantInfo {
        private Long profileId;
        private String username;
        private String avatarUrl;
        private ProfileType profileType;
    }
}