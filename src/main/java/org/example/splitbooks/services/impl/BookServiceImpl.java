package org.example.splitbooks.services.impl;

import org.example.splitbooks.dto.request.BooksSearchRequest;
import org.example.splitbooks.dto.request.ReviewRequest;
import org.example.splitbooks.dto.response.BookDetailsResponse;
import org.example.splitbooks.dto.response.BookWithReviewsResponse;
import org.example.splitbooks.dto.response.BooksResponse;
import org.example.splitbooks.dto.response.ReviewResponse;
import org.example.splitbooks.entity.*;
import org.example.splitbooks.repositories.*;
import org.example.splitbooks.services.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ProfileRepository profileRepository;
    private final BookProfileRepository bookProfileRepository;
    private final RestTemplate restTemplate;
    private final BookReviewRepository bookReviewRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    @Value("${google.books.api.key}")
    private String apiKey;

    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes";

    public BookServiceImpl(UserRepository userRepository, BookRepository bookRepository, ProfileRepository profileRepository,
                           BookProfileRepository bookProfileRepository,BookReviewRepository bookReviewRepository,RestTemplate restTemplate) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.bookProfileRepository = bookProfileRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.restTemplate = restTemplate;
    }

    public BookDetailsResponse showBook(String volumeId){
        String url = UriComponentsBuilder
                .fromHttpUrl(GOOGLE_BOOKS_API_URL)
                .pathSegment(volumeId)
                .queryParam("key", apiKey)
                .queryParam("langRestrict", "en")
                .toUriString();

        BookDetailsResponse book = restTemplate.getForObject(url, BookDetailsResponse.class);
        if (book != null) {
            return book;
        }
        throw new RuntimeException("Book not found");
    }

    public BookWithReviewsResponse getBookWithReviews(String volumeId) {
        BookDetailsResponse bookDetails = showBook(volumeId);

        List<BookReview> reviewEntities = bookReviewRepository.findByVolumeId(volumeId);

        List<ReviewResponse> reviewResponses = reviewEntities.stream().map(review -> {
            ReviewResponse response = new ReviewResponse();
            response.setReviewId(review.getBookReviewId());
            response.setVolumeId(review.getVolumeId());
            response.setUsername(review.getProfile().getUsername());
            response.setReviewText(review.getReviewText());
            response.setRating(review.getRating());
            response.setCreatedAt(review.getCreatedAt());

            return response;
        }).toList();

        BookWithReviewsResponse response = new BookWithReviewsResponse();
        response.setBook(bookDetails);
        response.setReviews(reviewResponses);
        return response;
    }
    public void addBookToLibrary(String volumeId) {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType())
                .orElseThrow(() -> new RuntimeException("Active profile not found"));

        Book book = bookRepository.findByVolumeId(volumeId);

        if (book == null) {
            BookDetailsResponse bookDetailsResponse = showBook(volumeId);

            book = new Book();
            book.setVolumeId(volumeId);
            book.setTitle(getNullSafeString(bookDetailsResponse.getVolumeInfo().getTitle()));
            book.setAuthor(getNullSafeString(bookDetailsResponse.getVolumeInfo().getAuthors().toString()));
            book.setDescription(getNullSafeString(bookDetailsResponse.getVolumeInfo().getDescription()));
            book.setImageUrl(getNullSafeString(bookDetailsResponse.getVolumeInfo().getImageLinks().getThumbnail()));

            Integer pageCount = bookDetailsResponse.getVolumeInfo().getPageCount();
            int pageCountValue = (pageCount != null) ? pageCount.intValue() : 0;
            book.setPageCount(pageCountValue);
            book.setPublisher(getNullSafeString(bookDetailsResponse.getVolumeInfo().getPublisher()));
            book.setPublishedDate(getNullSafeString(bookDetailsResponse.getVolumeInfo().getPublishedDate()));
            bookRepository.save(book);
        }

        BookProfile existingBookProfile = bookProfileRepository.findByBookAndProfile(book, profile);

        if (existingBookProfile == null) {
            BookProfile bookProfile = new BookProfile();
            bookProfile.setBook(book);
            bookProfile.setProfile(profile);
            bookProfile.setAddedAt(LocalDateTime.now());

            bookProfileRepository.save(bookProfile);
        } else {
            throw new RuntimeException("This book is already in your library.");
        }
    }
    private String getNullSafeString(String value) {
        return value != null ? value : "null";  // Return "null" string if the value is null
    }
    public void removeBookFromLibrary(String volumeId) {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType())
                .orElseThrow(() -> new RuntimeException("Active profile not found"));

        if (profile == null) {
            throw new RuntimeException("Profile not found for user");
        }

        Book book = bookRepository.findByVolumeId(volumeId);
        if (book == null) {
            throw new RuntimeException("Book not found in database");
        }

        BookProfile bookProfile = bookProfileRepository.findByBookAndProfile(book, profile);
        if (bookProfile == null) {
            throw new RuntimeException("Book not found in your library");
        }

        bookProfileRepository.delete(bookProfile);
    }

    public BooksResponse searchBooks(BooksSearchRequest request) {
        String query = buildQuery(request);

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }

        List<BooksResponse.Item> allItems = new ArrayList<>();
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
                ResponseEntity<BooksResponse> response =
                        restTemplate.getForEntity(url, BooksResponse.class);
                BooksResponse googleBooksResponse = response.getBody();

                if (googleBooksResponse != null && googleBooksResponse.getItems() != null) {
                    List<BooksResponse.Item> englishItems = googleBooksResponse.getItems().stream()
                            .filter(item -> item.getVolumeInfo() != null && "en".equalsIgnoreCase(item.getVolumeInfo().getLanguage()))
                            .toList();

                    allItems.addAll(englishItems);
                    totalItems = googleBooksResponse.getTotalItems();
                    startIndex += maxResults;

                } else {
                    break;
                }
            } catch (Exception e) {
                logger.error("Error fetching books from Google API", e);
                throw new RuntimeException("Failed to fetch books from Google API", e);
            }
        }

        BooksResponse result = new BooksResponse();
        result.setItems(allItems);
        result.setTotalItems(allItems.size());
        return result;
    }

    public ReviewResponse addReview(ReviewRequest request) {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType())
                .orElseThrow(() -> new RuntimeException("Active profile not found"));

        BookReview review = new BookReview();
        review.setVolumeId(request.getVolumeId());
        review.setProfile(profile);
        review.setReviewText(request.getReviewText());
        review.setRating(request.getRating());
        review.setCreatedAt(LocalDateTime.now());

        if (request.getParentReviewId() != null) {
            BookReview parent = bookReviewRepository.findById(request.getParentReviewId())
                    .orElseThrow(() -> new RuntimeException("Parent review not found"));
            review.setParent(parent);

        }

        BookReview savedReview = bookReviewRepository.save(review);

        ReviewResponse response = new ReviewResponse();
        response.setReviewId(savedReview.getBookReviewId());
        response.setVolumeId(savedReview.getVolumeId());
        response.setReviewText(savedReview.getReviewText());
        response.setRating(savedReview.getRating());
        response.setCreatedAt(savedReview.getCreatedAt());
        response.setUsername(savedReview.getProfile().getUsername());
//        if(request.getParentReviewId() != null) {
//            BookReview parent = bookReviewRepository.findById(request.getParentReviewId())
//                    .orElseThrow(() -> new RuntimeException("Parent review not found"));
//            response.setParentReviewId(parent.getParent().getBookReviewId());
//            System.out.println(parent.getParent().getBookReviewId());
//        }

        return response;
    }

    public void removeReview(Long reviewId) {
        Long userId = getAuthenticatedUserId();
        User user = getUserById(userId);

        Profile profile = profileRepository.findByUser_UserIdAndType(userId, user.getActiveProfileType())
                .orElseThrow(() -> new RuntimeException("Active profile not found"));


        BookReview review = bookReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));


        if (!review.getProfile().getProfileId().equals(profile.getProfileId())) {
            throw new RuntimeException("You can only delete your own reviews.");
        }

        bookReviewRepository.deleteById(reviewId);
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

    private Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getPrincipal().toString());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

}
