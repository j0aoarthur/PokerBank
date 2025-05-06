package com.j0aoarthur.pokerbank.repositories;

import com.j0aoarthur.pokerbank.entities.PlayerRanking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRankingRepository extends JpaRepository<PlayerRanking, Long> {
}
