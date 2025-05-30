package com.j0aoarthur.pokerbank.controllers;

import com.j0aoarthur.pokerbank.DTOs.request.GamePlayerRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.request.GameRequestDTO;
import com.j0aoarthur.pokerbank.DTOs.response.GameInfoDTO;
import com.j0aoarthur.pokerbank.DTOs.response.GamePlayerBalanceDTO;
import com.j0aoarthur.pokerbank.entities.Game;
import com.j0aoarthur.pokerbank.entities.GamePlayer;
import com.j0aoarthur.pokerbank.services.GamePlayerService;
import com.j0aoarthur.pokerbank.services.GameService;
import jakarta.validation.Valid;
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

    @GetMapping("/{gameId}/players")
    public ResponseEntity<List<GamePlayerBalanceDTO>> getGamePlayersWithBalance(@PathVariable Long gameId) {
        List<GamePlayer> balances = gamePlayerService.getGamePlayersByGame(gameId);
        return ResponseEntity.ok(balances.stream().map(GamePlayerBalanceDTO::new).toList());
    }


}
