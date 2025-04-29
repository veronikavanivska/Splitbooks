package org.example.splitbooks.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Genre")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int genreId;

    @Column(unique = true, nullable = false)
    private String genreName;
}
