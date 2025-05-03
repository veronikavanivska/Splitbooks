package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    boolean existsByUsername(String username);

    Profile findByUser_UserId(Long userId); // ✅ CORRECT


}
