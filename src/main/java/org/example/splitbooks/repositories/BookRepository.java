package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByVolumeId(String volumeId);

}
