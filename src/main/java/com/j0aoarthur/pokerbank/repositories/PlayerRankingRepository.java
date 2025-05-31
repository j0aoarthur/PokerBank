package com.j0aoarthur.pokerbank.repositories;

import com.j0aoarthur.pokerbank.entities.PlayerRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRankingRepository extends JpaRepository<PlayerRanking, Long> {
    Optional<PlayerRanking> findByPlayerId(Long playerId);
}
