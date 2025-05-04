package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookReviewRepository extends JpaRepository<BookReview, Long> {
    List<BookReview> findByVolumeId(String volumeId);
}
