package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.DTOs.request.GamePlayerRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.request.GameRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.request.UpdateGamePlayerDTO;
import com.j0aoarthur.pokerbank.DTOs.response.*;
import com.j0aoarthur.pokerbank.entities.ChipCount;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.services.GamePlayerService;
import com.j0aoarthur.pokerbank.services.GameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody @Valid GameRequestDTO gameDTO) {
        Game createdGame = gameService.createGame(gameDTO);
        return ResponseEntity.ok(createdGame);
    }

    @GetMapping
    public ResponseEntity<Page<GameDTO>> getAllGames(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {

        Page<Game> games = gameService.getAllGames(page, size);
        return ResponseEntity.ok(games.map(GameDTO::new));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameInfoDTO> getGameInfo(@PathVariable Long gameId) {
        GameInfoDTO gameInfo = gameService.getGameInfo(gameId);
        return ResponseEntity.ok(gameInfo);
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

    @PutMapping("/{gameId}/players/{playerId}")
    public ResponseEntity<GamePlayer> updateGamePlayer(@PathVariable Long gameId, @PathVariable Long playerId, @RequestBody @Valid UpdateGamePlayerDTO dto) {
        GamePlayer updatedGamePlayer = gamePlayerService.updateGamePlayer(gameId, playerId, dto);
        return ResponseEntity.ok(updatedGamePlayer);
    }

    @GetMapping("/{gameId}/players")
    public ResponseEntity<List<GamePlayerBalanceDTO>> getGamePlayersByGame(@PathVariable Long gameId) {
        List<GamePlayer> balances = gamePlayerService.getGamePlayersByGame(gameId);
        return ResponseEntity.ok(balances.stream().map(GamePlayerBalanceDTO::new).toList());
    }

    @GetMapping("/{gameId}/players/{playerId}")
    public ResponseEntity<GamePlayerInfoDTO> getGamePlayerByGameAndPlayer(@PathVariable Long gameId, @PathVariable Long playerId) {
        GamePlayer gamePlayer = gamePlayerService.getGamePlayerByGameAndPlayer(gameId, playerId);
        List<ChipCount> chipCounts = gamePlayerService.getChipCountsByGamePlayer(gamePlayer.getId());

        GamePlayerInfoDTO gamePlayerInfo = new GamePlayerInfoDTO(gamePlayer, chipCounts.stream().map(ChipCountDTO::new).toList());
        return ResponseEntity.ok(gamePlayerInfo);
    }


}
