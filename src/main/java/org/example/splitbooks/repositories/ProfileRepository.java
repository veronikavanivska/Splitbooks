package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.Profile;
import org.example.splitbooks.entity.ProfileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    boolean existsByUsername(String username);

    Optional<Profile> findByUser_UserIdAndType(Long userId, ProfileType type);
    Profile findByUser_UserId(Long userId);


}
