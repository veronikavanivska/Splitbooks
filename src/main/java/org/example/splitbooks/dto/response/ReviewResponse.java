package org.example.splitbooks.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long reviewId;
    private String volumeId;
    private String reviewText;
    private int rating;
    private String username;
    private LocalDateTime createdAt;
    private Long parentReviewId;
}