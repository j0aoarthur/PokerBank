package com.j0aoarthur.pokerbank.services;

import com.j0aoarthur.pokerbank.dtos.request.GameRequestDTO;
import com.j0aoarthur.pokerbank.dtos.response.GameInfoDTO;
import com.j0aoarthur.pokerbank.entities.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GameService {
    Game createGame(GameRequestDTO gameRequestDTO);
    void deleteGame(Long id);
    Page<Game> getGames(Pageable pageable);
    Game getGameById(Long id);
    List<Game> getAllGames();
    List<Game> getLatestGames();
    GameInfoDTO getGameInfoById(Long id);
    void checkGameFinished(Long gameId);
}
