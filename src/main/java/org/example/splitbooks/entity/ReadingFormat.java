package org.example.splitbooks.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name= "readingFormat")
public class ReadingFormat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formatId;

    @Column(unique = true, nullable = false)
    private String formatName;
}
