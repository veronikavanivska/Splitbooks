package org.example.splitbooks.repositories;


import org.example.splitbooks.entity.Book;
import org.example.splitbooks.entity.BookProfile;
import org.example.splitbooks.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookProfileRepository extends JpaRepository<BookProfile, Long> {
    List<BookProfile> findByProfile(Profile profile);
    List<BookProfile> findByBook(Book book);
    BookProfile findByBookAndProfile(Book book, Profile profile);

    boolean existsByBookAndProfile(Book book, Profile profile);
    List<BookProfile> findByBookProfileId(Long ProfileId);
    List<BookProfile> findByProfile_ProfileId(Long profileId);

}
