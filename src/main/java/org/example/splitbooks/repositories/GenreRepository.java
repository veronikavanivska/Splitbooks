package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    List<Genre> findAll();

    List<Genre> findByGenreIdIn(List<Long> genreIds); // ✅ This is the correct Spring JPA syntax

}
