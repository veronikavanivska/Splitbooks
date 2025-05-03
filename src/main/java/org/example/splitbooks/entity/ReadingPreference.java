package org.example.splitbooks.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ReadingPreference")
public class ReadingPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long readingId;

    @ManyToOne
    @JoinColumn(name = "format_id")
    private ReadingFormat format;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    @ManyToOne
    @JoinColumn(name = "profileid")
    private Profile profile;
}
