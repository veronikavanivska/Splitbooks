package org.example.splitbooks.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profileid", nullable = false)
    private Profile receiver;

    @Enumerated(EnumType.STRING)
    private  NotificationType type;

    private LocalDateTime created = LocalDateTime.now();
}
