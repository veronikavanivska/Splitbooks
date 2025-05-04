package org.example.splitbooks.services;

import org.example.splitbooks.dto.request.BooksSearchRequest;
import org.example.splitbooks.dto.response.BookDetailsResponse;
import org.example.splitbooks.dto.response.BooksResponse;
import org.springframework.stereotype.Component;

@Component
public interface BookService {
    public BooksResponse searchBooks(BooksSearchRequest request);
    public BookDetailsResponse showBook(String bookId);

}
