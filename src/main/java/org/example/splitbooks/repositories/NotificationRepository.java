package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.Notification;

import org.example.splitbooks.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByReceiver(Profile receiver);

}
