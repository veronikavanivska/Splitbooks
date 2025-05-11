package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.QuoteMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteMatchRepository extends JpaRepository<QuoteMatch, Long> {

}
