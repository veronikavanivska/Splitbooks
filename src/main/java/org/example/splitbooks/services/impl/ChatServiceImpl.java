package org.example.splitbooks.services.impl;

import org.example.splitbooks.dto.request.CreateGroupChatRequest;
import org.example.splitbooks.dto.request.CreatePrivateChatRequest;
import org.example.splitbooks.dto.response.ChatResponse;
import org.example.splitbooks.dto.response.PageResponse;
import org.example.splitbooks.dto.response.ShortChatResponse;
import org.example.splitbooks.entity.*;
import org.example.splitbooks.repositories.ChatParticipantRepository;
import org.example.splitbooks.repositories.ChatRepository;
import org.example.splitbooks.repositories.ProfileRepository;
import org.example.splitbooks.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class ChatServiceImpl {

    private final ChatRepository chatRepository;
    private final ProfileRepository profileRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;
    private final CloudinaryServiceImpl cloudinaryService;
    private final String DEFAUL_AVATAR = "https://res.cloudinary.com/dvzwpbmt7/image/upload/v1748786650/default-avatar_w2tksc.png";
    private final String DEFAULT_GROUP = "https://res.cloudinary.com/dvzwpbmt7/image/upload/v1749493812/group-chat-svgrepo-com_pi7icv.png";

    public ChatServiceImpl(ChatRepository chatRepository,CloudinaryServiceImpl cloudinaryService, ProfileRepository profileRepository, ChatParticipantRepository chatParticipantRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.profileRepository = profileRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;

    }

    public ChatResponse createGroupChat(CreateGroupChatRequest request, MultipartFile file) {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId,user.getActiveProfileType()).orElseThrow(()-> new RuntimeException(
                "Active profile not found"
        ));

        List<Profile> participants = profileRepository.findAllById(request.getParticipantIds());
        if (participants.stream().noneMatch(p -> p.getProfileId().equals(profile.getProfileId()))) {
            participants.add(profile);
        }

        if (participants.size() < 3) {
            throw new RuntimeException("Group chat must have at least 3 participants (including the creator).");
        }
        if (profile.getType() == ProfileType.ANONYMOUS) {
            if (!(request.getGroupChatType() == GroupChatType.ANONYMOUS_ONLY ||
                    request.getGroupChatType() == GroupChatType.MIXED)) {
                throw new RuntimeException("Anonymous users can only create ANONYMOUS_ONLY or MIXED group chats");
            }
        }else if(profile.getType() == ProfileType.PUBLIC){
            if (!(request.getGroupChatType() == GroupChatType.PUBLIC_ONLY ||
                    request.getGroupChatType() == GroupChatType.MIXED)) {
                throw new RuntimeException("Public users can only create PUBLIC_ONLY or MIXED group chats");
            }
        }
        String photo;
        if (file == null || file.isEmpty()) {
            photo = DEFAULT_GROUP;
        } else {
            photo = cloudinaryService.uploadAvatar(file);
        }


        validateGroupParticipants(participants, request.getGroupChatType());
        Optional<Chat> existingChat = findExistingGroupChatWithParticipants(participants, request.getGroupChatType(),request.getGroupName());
        if (existingChat.isPresent()) {
            return mapChatToResponse(existingChat.get());
        }

        Chat chat = new Chat();
        chat.setGroup(true);
        chat.setGroupChatType(request.getGroupChatType());
        chat.setGroupName(request.getGroupName());
        chat.setChatType(ChatType.GROUP);
        chat.setChatPhotoUrl(photo);

        chat = chatRepository.save(chat);

        for (Profile participant : participants) {
            ChatParticipant cp = new ChatParticipant();
            cp.setChat(chat);
            cp.setParticipant(participant);
            chatParticipantRepository.save(cp);
        }

        chat.setParticipants(chatParticipantRepository.findByChat(chat));

        ChatResponse response = new ChatResponse();
        response.setChatId(chat.getChatId());
        response.setGroup(chat.isGroup());
        response.setGroupName(chat.getGroupName());
        response.setChatType(chat.getChatType());
        response.setGroupChatType(chat.getGroupChatType());
        response.setChatPhotoUrl(photo);

        List<ChatResponse.ParticipantInfo> participantResponses = chat.getParticipants().stream()
                .map(cp -> {
                    ChatResponse.ParticipantInfo pr = new ChatResponse.ParticipantInfo();
                    pr.setProfileId(cp.getParticipant().getProfileId());
                    pr.setUsername(cp.getParticipant().getUsername());
                    pr.setProfileType(cp.getParticipant().getType());
                    pr.setAvatarUrl(cp.getParticipant().getAvatarUrl());
                    return pr;
                })
                .collect(Collectors.toList());

        response.setParticipants(participantResponses);

        return response;
    }
    private Optional<Chat> findExistingGroupChatWithParticipants(List<Profile> participants, GroupChatType groupChatType,String groupName) {

        List<Chat> groupChats = chatRepository.findByIsGroupTrueAndGroupChatType(groupChatType);

        Set<Long> participantIdsSet = participants.stream()
                .map(Profile::getProfileId)
                .collect(Collectors.toSet());

        for (Chat chat : groupChats) {

            if (!chat.getGroupName().equalsIgnoreCase(groupName)) {
                continue;
            }
            List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChat(chat);
            Set<Long> chatParticipantIds = chatParticipants.stream()
                    .map(cp -> cp.getParticipant().getProfileId())
                    .collect(Collectors.toSet());

            if (participantIdsSet.equals(chatParticipantIds)) {
                return Optional.of(chat);
            }
        }
        return Optional.empty();
    }

    public ChatResponse getChatById(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));



        return mapChatToResponse(chat);
    }

    public void deleteChat(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        chatRepository.delete(chat);
    }



    private ChatResponse mapChatToResponse(Chat chat) {
        ChatResponse response = new ChatResponse();
        response.setChatId(chat.getChatId());
        response.setGroup(chat.isGroup());
        response.setGroupName(chat.getGroupName());
        response.setChatType(chat.getChatType());
        response.setGroupChatType(chat.getGroupChatType());
        response.setChatPhotoUrl(chat.getChatPhotoUrl());

        List<ChatResponse.ParticipantInfo> participantResponses = chat.getParticipants().stream()
                .map(cp -> {
                    ChatResponse.ParticipantInfo pr = new ChatResponse.ParticipantInfo();
                    pr.setProfileId(cp.getParticipant().getProfileId());
                    pr.setUsername(cp.getParticipant().getUsername());
                    pr.setProfileType(cp.getParticipant().getType());
                    pr.setAvatarUrl(cp.getParticipant().getAvatarUrl());
                    return pr;
                })
                .collect(Collectors.toList());

        response.setParticipants(participantResponses);

        return response;
    }

    public ChatResponse createPrivateChat(CreatePrivateChatRequest request) {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId,user.getActiveProfileType()).orElseThrow(()-> new RuntimeException(
                "Active profile not found"
        ));

        Profile otherParticipant = profileRepository.findByProfileId(request.getOtherParticipantId()).orElseThrow(()-> new RuntimeException("User not found"));

        List<Profile> participants = List.of(profile, otherParticipant);


        Chat existingChat = chatRepository.findPrivateChatBetweenProfiles(profile.getProfileId(), otherParticipant.getProfileId());
        if (existingChat != null) {

            ChatResponse response = new ChatResponse();
            response.setChatId(existingChat.getChatId());
            response.setGroup(existingChat.isGroup());
            response.setChatType(existingChat.getChatType());
            response.setGroupChatType(existingChat.getGroupChatType());

            if (!existingChat.isGroup()) {
                String chatName = existingChat.getParticipants().stream()
                        .map(ChatParticipant::getParticipant)
                        .filter(p -> !p.getProfileId().equals(profile.getProfileId()))
                        .map(Profile::getUsername)
                        .findFirst()
                        .orElse("Unknown User");
                response.setGroupName(chatName);
            } else {
                response.setGroupName(existingChat.getGroupName());
            }

            List<ChatResponse.ParticipantInfo> participantResponses = existingChat.getParticipants().stream()
                    .map(cp -> {
                        ChatResponse.ParticipantInfo pr = new ChatResponse.ParticipantInfo();
                        pr.setProfileId(cp.getParticipant().getProfileId());
                        pr.setUsername(cp.getParticipant().getUsername());
                        pr.setProfileType(cp.getParticipant().getType());
                        pr.setAvatarUrl(cp.getParticipant().getAvatarUrl());
                        response.setChatPhotoUrl(cp.getParticipant().getAvatarUrl());
                        return pr;
                    })
                    .collect(Collectors.toList());

            response.setParticipants(participantResponses);

            return response;
        }

        Chat chat = new Chat();
        chat.setGroup(false);
        chat.setChatType(ChatType.PRIVATE);
        chat.setGroupName(null);

        chat = chatRepository.save(chat);

        for (Profile participant : participants) {
            ChatParticipant cp = new ChatParticipant();
            cp.setChat(chat);
            cp.setParticipant(participant);
            chatParticipantRepository.save(cp);
        }

        chat.setParticipants(chatParticipantRepository.findByChat(chat));

        ChatResponse response = new ChatResponse();
        response.setChatId(chat.getChatId());
        response.setGroup(chat.isGroup());
        response.setChatType(chat.getChatType());
        response.setGroupChatType(chat.getGroupChatType());

        String chatName = participants.stream()
                .filter(p -> !p.getProfileId().equals(profile.getProfileId()))
                .map(Profile::getUsername)
                .findFirst()
                .orElse("Unknown User");

        response.setGroupName(chatName);

        List<ChatResponse.ParticipantInfo> participantResponses = chat.getParticipants().stream()
                .map(cp -> {
                    ChatResponse.ParticipantInfo pr = new ChatResponse.ParticipantInfo();
                    pr.setProfileId(cp.getParticipant().getProfileId());
                    pr.setUsername(cp.getParticipant().getUsername());
                    pr.setProfileType(cp.getParticipant().getType());
                    pr.setAvatarUrl(cp.getParticipant().getAvatarUrl());
                    response.setChatPhotoUrl(cp.getParticipant().getAvatarUrl());
                    return pr;
                })
                .collect(Collectors.toList());

        response.setParticipants(participantResponses);

        return response;

    }

    public PageResponse<ShortChatResponse> allChats(Pageable pageable) {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType()).orElseThrow(() -> new RuntimeException("Active profile not found"));

        Page<Chat> chats = chatRepository.findAllChatsByProfileIdOrderByLastUpdatedDesc(profile.getProfileId(), pageable);

        Page<ShortChatResponse> shortChatResponses = chats.map(chat-> {

            if(chat.getChatType() == ChatType.GROUP) {
                return new ShortChatResponse(chat.getChatId(), chat.getGroupName(), chat.getChatPhotoUrl(), chat.isGroup(), chat.getLastUpdated()) ;
            } else {
                return chat.getParticipants().stream()
                        .filter(chatParticipant -> !chatParticipant.getParticipant().getProfileId().equals(profile.getProfileId()))
                        .findFirst()
                        .map(other -> {
                            String otherName = other.getParticipant().getUsername();
                            String otherAvatarUrl = other.getParticipant().getAvatarUrl();
                            System.out.println(otherAvatarUrl);
                            return new ShortChatResponse(chat.getChatId(), otherName, otherAvatarUrl,  chat.isGroup(), chat.getLastUpdated());
                        })
                        .orElseGet(() -> new ShortChatResponse(chat.getChatId(), "Private Chat", DEFAUL_AVATAR,  chat.isGroup(), chat.getLastUpdated()));
            }

        });
        return new PageResponse<>(shortChatResponses);

    }


    private void validateGroupParticipants(List<Profile> participants, GroupChatType groupChatType) {
        switch (groupChatType) {
            case ANONYMOUS_ONLY:
                boolean allAnonymous = participants.stream()
                        .allMatch(p -> p.getType() == ProfileType.ANONYMOUS);
                if (!allAnonymous) {
                    throw new RuntimeException("All participants must be anonymous for ANONYMOUS_ONLY group chats.");
                }
                break;

            case PUBLIC_ONLY:
                boolean allPublic = participants.stream()
                        .allMatch(p -> p.getType() == ProfileType.PUBLIC);
                if (!allPublic) {
                    throw new RuntimeException("All participants must be public for PUBLIC_ONLY group chats.");
                }
                break;

            case MIXED:
                break;

            default:
                throw new RuntimeException("Unsupported group chat type: " + groupChatType);
        }
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
