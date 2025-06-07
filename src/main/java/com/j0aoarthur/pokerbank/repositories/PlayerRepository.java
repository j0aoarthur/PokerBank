package com.j0aoarthur.pokerbank.repositories;

import com.j0aoarthur.pokerbank.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("SELECT p FROM Player p WHERE p.id NOT IN (SELECT gp.player.id FROM GamePlayer gp WHERE gp.game.id = :gameId) ORDER BY p.name")
    List<Player> findPlayersNotInGameOrderByName(@Param("gameId") Long gameId);
}
