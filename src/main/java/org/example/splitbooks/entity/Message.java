package org.example.splitbooks.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    private Chat chat;

    @ManyToOne
    private Profile sender;

    private String content;

    private LocalDateTime timestamp = LocalDateTime.now();

    private boolean delivered = false;

    private Long recipientId;
}
