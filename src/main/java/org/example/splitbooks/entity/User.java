package org.example.splitbooks.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;


@Data
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long UserId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;



    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Profile> profiles;

    @Enumerated(EnumType.STRING)
    private ProfileType activeProfileType; // PUBLIC or ANONYMOUS

}
