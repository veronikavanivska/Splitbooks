package org.example.splitbooks.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "BookProfile")
public class BookProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    // Optionally store some extra data, like date added
    private LocalDateTime addedAt;

    // Getters and setters
}
