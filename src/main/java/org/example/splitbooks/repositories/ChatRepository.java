package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.Chat;
import org.example.splitbooks.entity.GroupChatType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("""
    SELECT c FROM Chat c
    JOIN c.participants cp1
    JOIN c.participants cp2
    WHERE c.isGroup = false 
      AND cp1.participant.profileId = :profileId1
      AND cp2.participant.profileId = :profileId2
""")
    Chat findPrivateChatBetweenProfiles(@Param("profileId1") Long profileId1, @Param("profileId2") Long profileId2);

    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.participant.profileId = :profileId ORDER BY c.lastUpdated DESC")
    Page<Chat> findAllChatsByProfileIdOrderByLastUpdatedDesc(Long profileId, Pageable pageable);

    Optional<Chat> findChatByChatId(Long chatId);

    @Query("""
    SELECT c FROM Chat c
    JOIN c.participants cp1
    WHERE cp1.participant.profileId = :profileId1
""")
    Page<Chat> findAllChatsByProfileId(@Param("profileId1") Long profileId , Pageable pageable) ;

    List<Chat> findByIsGroupTrueAndGroupChatType(GroupChatType groupChatType);
}
