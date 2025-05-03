    package org.example.splitbooks.entity;

    import jakarta.persistence.*;
    import lombok.Data;

    @Data
    @Entity
    @Table(name = "Language")
    public class Language {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long languageId;

        @Column(unique = true, nullable = false)
        private String languageName;

    }
