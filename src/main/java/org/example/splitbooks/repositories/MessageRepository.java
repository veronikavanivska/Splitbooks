package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.Chat;
import org.example.splitbooks.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatOrderByTimestampAsc(Chat chat);
    Page<Message> findByChat(Chat chat, Pageable pageable);
}
