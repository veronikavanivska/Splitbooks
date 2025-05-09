package org.example.splitbooks.services;

import org.example.splitbooks.dto.request.BooksSearchRequest;
import org.example.splitbooks.dto.request.ReviewRequest;
import org.example.splitbooks.dto.response.BookDetailsResponse;
import org.example.splitbooks.dto.response.BookWithReviewsResponse;
import org.example.splitbooks.dto.response.BooksResponse;
import org.example.splitbooks.dto.response.ReviewResponse;
import org.springframework.stereotype.Component;

@Component
public interface BookService {
    public BooksResponse searchBooks(BooksSearchRequest request);
    public BookDetailsResponse showBook(String bookId);
    public BookWithReviewsResponse getBookWithReviews(String volumeId);
    public void addBookToLibrary(String volumeId);
    public void removeBookFromLibrary(String volumeId);
    public ReviewResponse addReview(ReviewRequest request);
    public void removeReview(Long reviewId);


}
