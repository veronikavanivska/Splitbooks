package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.QuoteSwipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteSwipeRepository extends JpaRepository<QuoteSwipe, Long> {
    Optional<QuoteSwipe> findBySwiperProfileIdAndTargetProfileId(Long swiperProfileId, Long targetProfileId);
    List<QuoteSwipe> findBySwiperProfileId(Long swiperProfileId);
    List<QuoteSwipe> findByTargetProfileIdAndLiked(Long targetProfileId, boolean liked);
    @Query("SELECT q.target.profileId FROM QuoteSwipe q WHERE q.swiper.profileId = :swiperId")
    List<Long> findTargetIdsBySwiperId(@Param("swiperId") Long swiperId);

}
