package org.example.splitbooks.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ChatPartisipant")
public class ChatParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Chat chat;

    @ManyToOne
    private Profile participant;
}
