package org.example.splitbooks.services.impl;

import org.example.splitbooks.dto.request.BooksSearchRequest;
import org.example.splitbooks.dto.response.BookDetailsResponse;
import org.example.splitbooks.dto.response.GoogleBooksResponse;
import org.example.splitbooks.entity.SearchType;
import org.example.splitbooks.services.GoogleBookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public final class GoogleBookServiceImpl implements GoogleBookService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(GoogleBookServiceImpl.class);

    @Value("${google.books.api.key}")
    private String apiKey;

    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes";

    public GoogleBookServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BookDetailsResponse seeBook(String bookId){
        String url = UriComponentsBuilder
                .fromHttpUrl(GOOGLE_BOOKS_API_URL)
                .pathSegment(bookId)
                .queryParam("key", apiKey)
                .queryParam("langRestrict", "en")
                .toUriString();

        BookDetailsResponse book = restTemplate.getForObject(url, BookDetailsResponse.class);
        if (book != null) {
            return book;
        }
        throw new RuntimeException("Book not found");
    }


    public GoogleBooksResponse searchBooks(BooksSearchRequest request) {
        String query = buildQuery(request);

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }

        List<GoogleBooksResponse.Item> allItems = new ArrayList<>();
        int startIndex = 0;
        int maxResults = 40;
        int totalItems = Integer.MAX_VALUE;

        while (startIndex < totalItems) {
            String url = UriComponentsBuilder
                    .fromHttpUrl(GOOGLE_BOOKS_API_URL)
                    .queryParam("q", query)
                    .queryParam("startIndex", startIndex)
                    .queryParam("maxResults", maxResults)
                    .queryParam("key", apiKey)
                    .toUriString();

            logger.info("Google Books API request: {}", url);

            try {
                ResponseEntity<GoogleBooksResponse> response =
                        restTemplate.getForEntity(url, GoogleBooksResponse.class);
                GoogleBooksResponse googleBooksResponse = response.getBody();

                if (googleBooksResponse != null && googleBooksResponse.getItems() != null) {
                    // Filter items to only include those explicitly marked as English
                    List<GoogleBooksResponse.Item> englishItems = googleBooksResponse.getItems().stream()
                            .filter(item -> item.getVolumeInfo() != null && "en".equalsIgnoreCase(item.getVolumeInfo().getLanguage()))
                            .toList();

                    allItems.addAll(englishItems);
                    totalItems = googleBooksResponse.getTotalItems();
                    startIndex += maxResults;

                } else {
                    break; // no more items
                }
            } catch (Exception e) {
                logger.error("Error fetching books from Google API", e);
                throw new RuntimeException("Failed to fetch books from Google API", e);
            }
        }

        GoogleBooksResponse result = new GoogleBooksResponse();
        result.setItems(allItems);
        result.setTotalItems(allItems.size());
        result.setKind("books#volumes"); // optional
        return result;
    }

    private String buildQuery(BooksSearchRequest request) {
        String searchQuery = request.getSearchQuery();
        SearchType type = request.getSearchType();

        if (searchQuery == null || type == null) return null;

        switch (type) {
            case BY_TITLE:
                return "intitle:" + encodeQuery(searchQuery);
            case BY_AUTHOR:
                return "inauthor:" + encodeQuery(searchQuery);
            case BY_GENRE :
                return "subject:" + searchQuery;
            default:
                return null;
        }
    }

    private String encodeQuery(String query) {
        try {
            return URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            logger.error("Error encoding query: {}", query, e);
            return query;
        }
    }
}
