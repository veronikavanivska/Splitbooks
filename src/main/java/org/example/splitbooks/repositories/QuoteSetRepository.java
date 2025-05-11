package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.Profile;
import org.example.splitbooks.entity.QuoteSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuoteSetRepository extends JpaRepository<QuoteSet, Long> {

    Optional<QuoteSet> findByProfile(Profile profile);
    Optional<QuoteSet> findByProfile_ProfileId(Long profileId);
}
