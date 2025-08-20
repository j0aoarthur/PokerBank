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

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_token_expiration")
    private Long verificationTokenExpiration;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiration")
    private Long resetTokenExpiration;



    public User(AuthRequestDTO authRequestDTO, String encodedPassword) {
        this.username = authRequestDTO.username();
        this.password = encodedPassword;
        this.email = authRequestDTO.email();
    }

    public void generateVerificationToken() {
        this.verificationToken = java.util.UUID.randomUUID().toString();
        this.verificationTokenExpiration = System.currentTimeMillis() + 24 * 60 * 60 * 1000; // 24 horas
    }

    public void generateResetToken() {
        this.resetToken = java.util.UUID.randomUUID().toString();
        this.resetTokenExpiration = System.currentTimeMillis() + 15 * 60 * 1000; // 15 minutos
    }
}
