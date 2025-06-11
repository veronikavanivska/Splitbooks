package org.example.splitbooks.services.impl;

import org.example.splitbooks.dto.request.SendMessageRequest;
import org.example.splitbooks.dto.response.PageResponse;
import org.example.splitbooks.dto.response.SendMessageResponse;
import org.example.splitbooks.entity.*;
import org.example.splitbooks.repositories.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
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

    public SendMessageResponse sendMessage(SendMessageRequest sendMessageRequest, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Profile sender = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType())
                .orElseThrow(() -> new RuntimeException("Active profile not found"));
//        Profile sender = profileRepository.findById(sendMessageRequest.getProfileId())
//                .orElseThrow(() -> new RuntimeException("Profile not found"));
        if (!sender.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Profile does not belong to user");
        }

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

        chat.setLastUpdated(LocalDateTime.now());
        chatRepository.save(chat);
        messageRepository.save(message);

        SendMessageResponse sendMessageResponse = new SendMessageResponse();
        sendMessageResponse.setMessageId(message.getMessageId());
        sendMessageResponse.setContent(message.getContent());
        sendMessageResponse.setSenderUsername(sender.getUsername());
        sendMessageResponse.setTimestamp(message.getTimestamp());
        sendMessageResponse.setSenderId(message.getSender().getProfileId());

        return sendMessageResponse;
    }
//    public SendMessageResponse sendMessage(SendMessageRequest sendMessageRequest, Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
//
//        Profile sender = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType())
//                .orElseThrow(() -> new RuntimeException("Active profile is not found"));
//
//        Chat chat = chatRepository.findChatByChatId(sendMessageRequest.getChatId())
//                .orElseThrow(() -> new RuntimeException("Chat not found"));
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
//        chat.setLastUpdated(LocalDateTime.now());
//        chatRepository.save(chat);
//        messageRepository.save(message);
//
//        SendMessageResponse sendMessageResponse = new SendMessageResponse();
//        sendMessageResponse.setMessageId(message.getMessageId());
//        sendMessageResponse.setContent(message.getContent());
//        sendMessageResponse.setSenderUsername(sender.getUsername());
//        sendMessageResponse.setTimestamp(LocalDateTime.now());
//        sendMessageResponse.setSenderId(userId);
//        return sendMessageResponse;
//    }

    public PageResponse<SendMessageResponse> getAllMessages(Long chatId,Pageable pageable) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));


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
