package org.example.splitbooks.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "BookReview")
public class BookReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookReviewId;

    private String volumeId;

    @ManyToOne
    @JoinColumn(name = "profileid")
    private Profile profile;

    private String reviewText;
    private int rating;

    private LocalDateTime createdAt;

}
