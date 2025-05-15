package org.example.splitbooks.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private BookReview parent;


    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookReview> replies = new ArrayList<>();

    private LocalDateTime createdAt;

}
