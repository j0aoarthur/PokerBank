package com.j0aoarthur.pokerbank.repositories;


import com.j0aoarthur.pokerbank.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByAccountId(Long accountId);

    void deleteByToken(String token);
}
