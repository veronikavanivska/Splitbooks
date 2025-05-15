package org.example.splitbooks.services;

import org.example.splitbooks.entity.Notification;
import org.example.splitbooks.entity.NotificationType;
import org.example.splitbooks.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public interface NotificationService {
    public Notification createAndSend(Profile receiver, String message, NotificationType type);
}
