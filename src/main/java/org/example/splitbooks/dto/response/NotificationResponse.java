package org.example.splitbooks.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.splitbooks.entity.Notification;
import org.example.splitbooks.entity.NotificationType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NotificationResponse {
        private Long id;
        private String username;
        private String message;
        private NotificationType type;
        private LocalDateTime created;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.message = notification.getMessage();
        this.type = notification.getType();
        this.created = notification.getCreated();
    }
}
