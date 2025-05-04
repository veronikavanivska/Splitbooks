package org.example.splitbooks.controllers;

import org.example.splitbooks.dto.request.BooksSearchRequest;
import org.example.splitbooks.dto.response.BookDetailsResponse;
import org.example.splitbooks.dto.response.GoogleBooksResponse;
import org.example.splitbooks.services.impl.GoogleBookServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class GoogleBooksController {

    private GoogleBookServiceImpl googleBooksService;

    public GoogleBooksController(GoogleBookServiceImpl googleBooksService) {
        this.googleBooksService = googleBooksService;
    }

    @GetMapping("/search")
    public ResponseEntity<GoogleBooksResponse> searchBooksByQuery(@RequestBody BooksSearchRequest request) {
        GoogleBooksResponse response = googleBooksService.searchBooks(request);

        if (response.getItems() == null || response.getItems().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDetailsResponse> getBookById(@PathVariable String bookId) {
        BookDetailsResponse bookResponse = googleBooksService.seeBook(bookId);

        return ResponseEntity.ok(bookResponse);
    }
}
