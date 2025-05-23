package com.j0aoarthur.pokerbank.repositories;

import com.j0aoarthur.pokerbank.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findTop3ByOrderByDateDesc();
}
