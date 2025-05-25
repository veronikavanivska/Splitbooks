package org.example.splitbooks.controllers;

import org.example.splitbooks.dto.request.CreateGroupChatRequest;
import org.example.splitbooks.dto.request.CreatePrivateChatRequest;
import org.example.splitbooks.dto.request.SendMessageRequest;
import org.example.splitbooks.dto.response.ChatResponse;
import org.example.splitbooks.dto.response.PageResponse;
import org.example.splitbooks.dto.response.SendMessageResponse;
import org.example.splitbooks.services.impl.ChatServiceImpl;
import org.example.splitbooks.services.impl.MessagingServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatServiceImpl chatService;
    private final MessagingServiceImpl messagingService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatServiceImpl chatService , MessagingServiceImpl messagingService , SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingService = messagingService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/group")
    public ResponseEntity<ChatResponse> createGroupChat(@RequestBody CreateGroupChatRequest request) {
        ChatResponse response = chatService.createGroupChat(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/private")
    public ResponseEntity<ChatResponse> createPrivateChat(@RequestBody CreatePrivateChatRequest request) {
        ChatResponse response = chatService.createPrivateChat(request);
        return ResponseEntity.ok(response);
    }

//    @MessageMapping("/chat.sendMessage")
//    public void sendMessage(SendMessageRequest sendMessageRequest) {
//        Long testUserId = 1L;
//        SendMessageResponse response = messagingService.sendMessage(sendMessageRequest);
//
//        messagingTemplate.convertAndSend("/topic/chat." + sendMessageRequest.getChatId(), response);
//    }
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(SendMessageRequest sendMessageRequest) {
        Long testUserId = 2L;
        SendMessageResponse response = messagingService.sendMessage(sendMessageRequest, testUserId);
        messagingTemplate.convertAndSend("/topic/chat." + sendMessageRequest.getChatId(), response);
    }

    @GetMapping("/{chatId}/messages")
    public PageResponse<SendMessageResponse> getChatMessages(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        return messagingService.getAllMessages(chatId, page, size);
    }
}

