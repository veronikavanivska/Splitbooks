package org.example.splitbooks.DTO;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;


    private boolean isAnonymous;

}
