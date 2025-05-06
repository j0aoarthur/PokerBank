package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.DTOs.request.GamePlayerRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.request.GameRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.response.GameInfoDTO;
import com.j0aoarthur.pokerbank.DTOs.response.GamePlayerBalanceDTO;
import com.j0aoarthur.pokerbank.DTOs.response.GamePlayerDTO;
import com.j0aoarthur.pokerbank.entities.ChipCount;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.repositories.ChipCountRepository;
import com.j0aoarthur.pokerbank.services.GamePlayerService;
import com.j0aoarthur.pokerbank.services.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private GamePlayerService gamePlayerService;

    @Autowired
    private ChipCountRepository chipCountRepository;

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody @Valid GameRequestDTO gameDTO) {
        Game createdGame = gameService.createGame(gameDTO);
        return ResponseEntity.ok(createdGame);
    }

    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<Game>> getLatestGames() {
        List<Game> latestGames = gameService.getLatestGames();
        return ResponseEntity.ok(latestGames);
    }


    @PostMapping("/add-player")
    public ResponseEntity<GamePlayer> addPlayerToGame(@RequestBody @Valid GamePlayerRequestDTO dto) {
        GamePlayer gamePlayer = gamePlayerService.addPlayerToGame(dto);
        return ResponseEntity.ok(gamePlayer);
    }

    @GetMapping("/{id}/players")
    public ResponseEntity<List<GamePlayerBalanceDTO>> getGamePlayersWithBalance(@PathVariable Long id) {
        List<GamePlayer> balances = gamePlayerService.getGamePlayersWithBalance(id);
        return ResponseEntity.ok(balances.stream().map(GamePlayerBalanceDTO::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity getGameInfo(@PathVariable Long id) {
        GameInfoDTO gameInfo = gamePlayerService.getGameInfo(id);
        return ResponseEntity.ok(gameInfo);
    }

    @GetMapping("/{gameId}/players/{playerId}")
    public ResponseEntity<GamePlayerDTO> getGamePlayerWithBalance(@PathVariable Long gameId, @PathVariable Long playerId) {
        GamePlayer gamePlayer = gamePlayerService.getGamePlayer(gameId, playerId);
        List<ChipCount> chipCounts = chipCountRepository.findByGamePlayerId(gamePlayer.getId());

        return ResponseEntity.ok(new GamePlayerDTO(gamePlayer,chipCounts));
    }


}
