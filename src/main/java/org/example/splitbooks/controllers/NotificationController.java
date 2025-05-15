package org.example.splitbooks.controllers;

import org.example.splitbooks.dto.response.AllNotificationResponse;
import org.example.splitbooks.entity.Notification;
import org.example.splitbooks.services.NotificationService;
import org.example.splitbooks.services.impl.NotificationServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private NotificationServiceImpl notificationService;

    public NotificationController(NotificationServiceImpl notificationService, SimpMessagingTemplate template) {
        this.notificationService = notificationService;

    }
//    @MessageMapping("/sendMessage") // Endpoint matching the JavaScript destination
//    @SendTo("/topic/notifications") // Broadcast to subscribers of this topic
//    public String sendMessage(String message) {
//        System.out.println("Received message: " + message); // Debugging log
//        return message; // Broadcast the message
//    }

    @GetMapping("/all")
    public ResponseEntity<List<AllNotificationResponse>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }
}

