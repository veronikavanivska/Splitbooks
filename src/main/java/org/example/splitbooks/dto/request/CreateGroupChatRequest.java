package org.example.splitbooks.dto.request;

import lombok.Data;
import org.example.splitbooks.entity.ChatType;
import org.example.splitbooks.entity.GroupChatType;

import java.util.List;

@Data
public class CreateGroupChatRequest {
    private String groupName;
    private List<Long> participantIds;
    private GroupChatType groupChatType;
}
