package org.example.splitbooks.controllers;

import org.example.splitbooks.dto.request.BooksSearchRequest;
import org.example.splitbooks.dto.request.ReviewRequest;
import org.example.splitbooks.dto.response.BookDetailsResponse;
import org.example.splitbooks.dto.response.BookWithReviewsResponse;
import org.example.splitbooks.dto.response.BooksResponse;
import org.example.splitbooks.dto.response.ReviewResponse;
import org.example.splitbooks.services.impl.BookServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BooksController {

    private BookServiceImpl booksService;

    public BooksController(BookServiceImpl booksService) {
        this.booksService = booksService;
    }
    @PostMapping("/{volumeId}/addReview")
    public ResponseEntity<ReviewResponse> addReview(
            @PathVariable String volumeId,
            @RequestBody ReviewRequest request) {
        request.setVolumeId(volumeId);
        ReviewResponse reviewResponse = booksService.addReview(request);
        return ResponseEntity.ok(reviewResponse);
    }
    @DeleteMapping("/removeReview/{reviewId}")
    public ResponseEntity<String> removeReview(@PathVariable Long reviewId) {
        booksService.removeReview(reviewId);
        return ResponseEntity.ok("Review deleted successfully.");
    }

    @PostMapping("/search")
    public ResponseEntity<BooksResponse> getBooks(
            @RequestBody BooksSearchRequest request,
            @RequestParam(defaultValue = "0") int startIndex,
            @RequestParam(defaultValue = "20") int maxResults
    ) {

        BooksResponse response = booksService.searchBooks(request, startIndex, maxResults);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/mybooks/{profileId}")
    public ResponseEntity<BooksResponse> getMyBooks(@PathVariable Long profileId) {
        BooksResponse response = booksService.getBooksByProfileId(profileId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{volumeId}")
    public ResponseEntity<BookWithReviewsResponse> getBookById(@PathVariable String volumeId) {
        BookWithReviewsResponse bookResponse = booksService.getBookWithReviews(volumeId);

        return ResponseEntity.ok(bookResponse);
    }
    @PostMapping("/{volumeId}/add")
    public ResponseEntity<?> addBookToLibrary(@PathVariable String volumeId) {

        booksService.addBookToLibrary(volumeId);
        return ResponseEntity.ok(Map.of("message", "Book added to your library."));
    }

    @DeleteMapping("/{volumeId}/remove")
    public ResponseEntity<String> removeBookFromLibrary(@PathVariable String volumeId) {
        booksService.removeBookFromLibrary(volumeId);
        return ResponseEntity.ok("Book removed from your library.");
    }
}
