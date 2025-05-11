package org.example.splitbooks.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "quote_match", uniqueConstraints = {@UniqueConstraint(columnNames = {"profile1_id", "profile2_id"})})
public class QuoteMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profile1_id", referencedColumnName = "profileId")
    private Profile profile1;

    @ManyToOne
    @JoinColumn(name = "profile2_id", referencedColumnName = "profileId")
    private Profile profile2;

}
