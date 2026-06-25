package com.j0aoarthur.pokerbank.repositories;

import com.j0aoarthur.pokerbank.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);

    Optional<Account> findByResetToken(String token);
    Optional<Account> findByVerificationToken(String token);

    Optional<Account> findByEmail(String email);
}
