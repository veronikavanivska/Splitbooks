package org.example.splitbooks.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "swipe")
public class QuoteSwipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "swiper_id", referencedColumnName = "profileId")
    private Profile swiper;

    @ManyToOne
    @JoinColumn(name = "target_id", referencedColumnName = "profileId")
    private Profile target;


    private boolean liked;

}
