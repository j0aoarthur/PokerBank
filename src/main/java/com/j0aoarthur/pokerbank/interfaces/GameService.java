package com.j0aoarthur.pokerbank.interfaces;

import com.j0aoarthur.pokerbank.DTOs.request.GameRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.response.GameInfoDTO;
import com.j0aoarthur.pokerbank.entities.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GameService {
    Game createGame(GameRequestDTO gameRequestDTO);
    void deleteGame(Long id);
    Page<Game> getGames(Pageable pageable);
    List<Game> getAllGames();
    List<Game> getLatestGames();
    GameInfoDTO getGameInfoById(Long id);
    void checkGameFinished(Long gameId);
}
