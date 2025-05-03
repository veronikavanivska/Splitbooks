package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.ReadingFormat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReadingFormatRepository extends JpaRepository<ReadingFormat, Long> {
    List<ReadingFormat> findAll();
}
