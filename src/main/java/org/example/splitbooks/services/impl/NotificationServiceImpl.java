package org.example.splitbooks.services.impl;
import org.example.splitbooks.dto.response.AllNotificationResponse;
import org.example.splitbooks.dto.response.NotificationResponse;
import org.example.splitbooks.entity.Notification;
import org.example.splitbooks.entity.NotificationType;
import org.example.splitbooks.entity.Profile;
import org.example.splitbooks.entity.User;
import org.example.splitbooks.repositories.NotificationRepository;
import org.example.splitbooks.repositories.ProfileRepository;
import org.example.splitbooks.repositories.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;


@Service
public class NotificationServiceImpl {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate template;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;


    public NotificationServiceImpl(NotificationRepository notificationRepository,ProfileRepository profileRepository, UserRepository userRepository, SimpMessagingTemplate template) {
        this.notificationRepository = notificationRepository;
        this.profileRepository = profileRepository;
        this.template = template;
        this.userRepository = userRepository;
    }

    public Notification createAndSend(Profile receiver, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setReceiver(receiver);
        notification.setMessage(message);
        notification.setType(type);

        Notification saved = notificationRepository.save(notification);

        template.convertAndSendToUser(
                receiver.getProfileId().toString(),
                "/queue/notifications",
                new NotificationResponse(saved)
        );

        return saved;
    }

    public List<AllNotificationResponse> getAllNotifications() {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType()).orElseThrow(()->new RuntimeException("Active profile not found"));

        return notificationRepository.findAllByReceiver(profile).stream().map(
                n-> new AllNotificationResponse(
                            n.getMessage(),
                            n.getCreated()
                )).toList();

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
