package org.example.splitbooks.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class BookWithReviewsResponse {
    private BookDetailsResponse book;
    private List<ReviewResponse> reviews;
}
