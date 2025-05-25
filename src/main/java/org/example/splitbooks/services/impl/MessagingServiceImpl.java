package org.example.splitbooks.services.impl;

import org.example.splitbooks.dto.request.SendMessageRequest;
import org.example.splitbooks.dto.response.PageResponse;
import org.example.splitbooks.dto.response.SendMessageResponse;
import org.example.splitbooks.entity.*;
import org.example.splitbooks.repositories.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessagingServiceImpl {


    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;


    public MessagingServiceImpl(ProfileRepository profileRepository, ChatRepository chatRepository,MessageRepository messageRepository, UserRepository userRepository,ChatParticipantRepository chatParticipantRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;

    }

//    public SendMessageResponse sendMessage(SendMessageRequest sendMessageRequest) {
//        Long userId = getAuthenticatedUserId();
//        User user = getUserById(userId);
//
//
//        Profile sender = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType()).orElseThrow(()->new RuntimeException("Active profile is not found "));
//
//        Chat chat = chatRepository.findChatByChatId(sendMessageRequest.getChatId()).orElseThrow(() -> new RuntimeException("Chat not found"));
//
//        if (!chatParticipantRepository.existsByChatAndParticipant(chat, sender)) {
//            throw new RuntimeException("User not part of chat");
//        }
//
//        Message message = new Message();
//        message.setChat(chat);
//        message.setSender(sender);
//        message.setContent(sendMessageRequest.getContent());
//        message.setTimestamp(LocalDateTime.now());
//        messageRepository.save(message);
//
//        SendMessageResponse sendMessageResponse = new SendMessageResponse();
//        sendMessageResponse.setMessageId(message.getMessageId());
//        sendMessageResponse.setContent(message.getContent());
//        sendMessageResponse.setSenderUsername(sender.getUsername());
//        sendMessageResponse.setTimestamp(LocalDateTime.now());
//        sendMessageResponse.setSenderId(userId);
//        return sendMessageResponse;
//
//
//
    //    }
    public SendMessageResponse sendMessage(SendMessageRequest sendMessageRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Profile sender = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType())
                .orElseThrow(() -> new RuntimeException("Active profile is not found"));

        Chat chat = chatRepository.findChatByChatId(sendMessageRequest.getChatId())
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        if (!chatParticipantRepository.existsByChatAndParticipant(chat, sender)) {
            throw new RuntimeException("User not part of chat");
        }

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(sendMessageRequest.getContent());
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);

        SendMessageResponse sendMessageResponse = new SendMessageResponse();
        sendMessageResponse.setMessageId(message.getMessageId());
        sendMessageResponse.setContent(message.getContent());
        sendMessageResponse.setSenderUsername(sender.getUsername());
        sendMessageResponse.setTimestamp(LocalDateTime.now());
        sendMessageResponse.setSenderId(userId);
        return sendMessageResponse;
    }

    public PageResponse<SendMessageResponse> getAllMessages(Long chatId, int page, int size) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").ascending());
        Page<Message> messages = messageRepository.findByChat(chat, pageable);

        Page<SendMessageResponse> dtoPage = messages.map(message -> {
            SendMessageResponse dto = new SendMessageResponse();
            dto.setMessageId(message.getMessageId());
            dto.setContent(message.getContent());
            dto.setTimestamp(message.getTimestamp());
            dto.setSenderId(message.getSender().getProfileId());
            dto.setSenderUsername(message.getSender().getUsername());
            return dto;
        });

        return new PageResponse<>(dtoPage);
    }


        private Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getPrincipal().toString());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }
}
