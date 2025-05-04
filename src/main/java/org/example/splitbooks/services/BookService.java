package org.example.splitbooks.services;

import org.example.splitbooks.dto.request.BooksSearchRequest;
import org.example.splitbooks.dto.response.GoogleBooksResponse;
import org.springframework.stereotype.Component;

@Component
public interface GoogleBookService {
    public GoogleBooksResponse searchBooks(BooksSearchRequest request);
}
