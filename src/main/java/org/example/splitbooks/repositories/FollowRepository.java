package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.Follow;
import org.example.splitbooks.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(Profile follower, Profile following);
    Optional<Follow> findByFollowerAndFollowing(Profile follower, Profile following);
    List<Follow> findAllByFollower(Profile follower);
    List<Follow> findAllByFollowing(Profile following);
}
