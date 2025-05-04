package org.example.splitbooks.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "Book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    private String volumeId;
    private String title;
    private int pageCount;
    private String publisher;
    private String publishedDate;

    @Column(length = 1000)
    private String author;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String imageUrl;
    @OneToMany(mappedBy = "book")
    private List<BookProfile> bookProfiles;


}
