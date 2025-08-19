package com.j0aoarthur.pokerbank.entities;

import com.j0aoarthur.pokerbank.DTOs.request.AuthRequestDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    public User(AuthRequestDTO authRequestDTO, String encodedPassword) {
        this.username = authRequestDTO.username();
        this.password = encodedPassword;
        this.email = authRequestDTO.email();
    }
}
