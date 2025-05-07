    package org.example.splitbooks.entity;

    import jakarta.persistence.*;
    import lombok.Data;

    import java.util.List;

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
        private String firstName;
        private String lastName;
        private String phone;

        @Enumerated(EnumType.STRING)
        private ProfileType type; // PUBLIC or ANONYMOUS

        private String username;
        private String avatarUrl;

        private boolean setupCompleted;

        @ManyToMany
        @JoinTable(
                name = "Profile_Genres",
                joinColumns = @JoinColumn(name = "profileid"),
                inverseJoinColumns = @JoinColumn(name = "genre_id")
        )
        private List<Genre> favoriteGenres;

        @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
        private List<ReadingPreference> readingPreferences;


        @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
        private List<BookProfile> bookProfiles;

        @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
        private List<BookReview> reviews;

    }
