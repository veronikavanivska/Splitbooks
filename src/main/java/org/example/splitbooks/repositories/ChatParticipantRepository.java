package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.Chat;
import org.example.splitbooks.entity.ChatParticipant;
import org.example.splitbooks.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
        List<ChatParticipant> findByChat(Chat chat);

        Boolean existsByChatAndParticipant(Chat chat, Profile participant);


}
