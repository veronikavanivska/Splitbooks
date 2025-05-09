package org.example.splitbooks.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "follows", uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}))
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who is following
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private Profile follower;

    // Who is being followed
    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private Profile following;

    @Column(name = "followed_at")
    private LocalDateTime followedAt = LocalDateTime.now();
}
