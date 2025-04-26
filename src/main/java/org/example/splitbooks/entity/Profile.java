    package org.example.splitbooks.entity;

    import jakarta.persistence.*;
    import lombok.Data;

    @Entity
    @Data
    @Table(name = "Profiles")
    public class Profile {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long Profileid;

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;

        @Enumerated(EnumType.STRING)
        private ProfileType type; // PUBLIC or ANONYMOUS

        private String username;
        private String avatarUrl;

    }
