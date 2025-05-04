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
    private Long id;

    private String volumeId;
    private String title;
    private String author;
    private String description;
    private String imageUrl;
    private int pageCount;
    private String publisher;
    private String publishedDate;

    @OneToMany(mappedBy = "book")
    private List<BookProfile> bookProfiles;


}
