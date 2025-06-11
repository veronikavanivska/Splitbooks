package org.example.splitbooks.controllers;

import org.example.splitbooks.dto.request.CreateGroupChatRequest;
import org.example.splitbooks.dto.request.CreatePrivateChatRequest;
import org.example.splitbooks.dto.request.SendMessageRequest;
import org.example.splitbooks.dto.response.ChatResponse;
import org.example.splitbooks.dto.response.PageResponse;
import org.example.splitbooks.dto.response.SendMessageResponse;
import org.example.splitbooks.dto.response.ShortChatResponse;
import org.example.splitbooks.services.impl.ChatServiceImpl;
import org.example.splitbooks.services.impl.MessagingServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

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

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponse> getChatInfo(@PathVariable Long chatId) {
        ChatResponse response = chatService.getChatById(chatId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<?> deleteChat(@PathVariable Long chatId) {
        chatService.deleteChat(chatId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/group")
    public ResponseEntity<ChatResponse> createGroupChat(@RequestPart("data") CreateGroupChatRequest request, @RequestPart(value = "avatar", required = false) MultipartFile file) {
        ChatResponse response = chatService.createGroupChat(request,file);
        return ResponseEntity.ok(response);
    }

        @PostMapping("/private")
        public ResponseEntity<ChatResponse> createPrivateChat(@RequestBody CreatePrivateChatRequest request) {
            ChatResponse response = chatService.createPrivateChat(request);
            return ResponseEntity.ok(response);
        }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload SendMessageRequest messageRequest, Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        SendMessageResponse response = messagingService.sendMessage(messageRequest, userId);
        messagingTemplate.convertAndSend("/topic/chat." + messageRequest.getChatId(), response);
    }

    @GetMapping("/{chatId}/messages")
    public PageResponse<SendMessageResponse> getChatMessages(
            @PathVariable Long chatId,@PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.ASC) Pageable pageable) {

        return messagingService.getAllMessages(chatId, pageable);
    }

    @GetMapping("/allChats")
    public PageResponse<ShortChatResponse> getAllChats(@PageableDefault(size = 10) Pageable pageable)
    {
        return chatService.allChats(pageable);
    }
}

