package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.ReadingPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingPreferenceRepository extends JpaRepository<ReadingPreference, Long> {

}
